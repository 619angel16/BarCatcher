from flask import jsonify, Response, request
from datetime import datetime
from helpers.connectBar_CC import get_ravendb_client, close_ravendb_client
from repository.CafebarsRepository import CafebarsRepository
from repository.CafedrinksRepository import CafedrinksRepository
from service.CafebarsService import CafebarsService
from service.CafedrinksService import CafedrinksService
from typing import Optional
from encode import CustomEncoder
import json

def register_blueprints(app):
    """Registry all app's blueprints"""
    # En el controller se debe de definir la blueprint (path de la petición que debe de registrar Flask)
    # Más info -> Buscar en google por Blueprints Flask
    #from controller.XXXX import nombre_blueprint
    
    # Registry endpoints paths#
    #app.register_blueprint(nombre_blueprint, url_prefix='/api/v1')
    
    # Registry basic paths
    register_basic_routes(app)


def register_basic_routes(app):
    """Registra basic app's paths"""
    
    client = get_ravendb_client()
    drinbar_repo = CafedrinksRepository(client)
    cafebar_repo = CafebarsRepository(client)
    drinkbar_service = CafedrinksService(drinbar_repo)
    cafebar_service = CafebarsService(cafebar_repo)

    @app.route('/')
    def index():
        return jsonify({
            "service": app.config['SERVICE_NAME'],
            "version": app.config['SERVICE_VERSION'],
            "status": "running",
            "endpoints-generales": {
                "health": "/health",
            },
            "endpoints-drinkbas": {
                "all-drinkbars": "/api/drinkbars",
                "limit-drinkbars": "/api/drinkbars/(limit)",
                "por-localidad": "/api/drinkbars/(locality)",
                "por-comida": "/api/drinkbars/food",
                "por-email": "/api/drinkbars/(email)",
                "por-cercania": "/api/drinkbars/cercano...query-params",
                "por-campo-especifico": "/api/drinkbars/campo...query-params"
            },
            "endpoints-cafebars": {
                "all-cafebars": "/api/cafebars",
                "limit-cafebars": "/api/cafebars/(limit)",
                "por-capacidad": "/api/cafebars/capacity/(capacidad)",
                "por-calle": "/api/cafebars/street/(street)",
                "por-cercania": "/api/cafebars/cercano...query-params",
                "por-campo-especifico": "/api/cafebars/campo...query-params"
            }
        })
    
    @app.route('/health', methods = ['GET'])
    def health_check():
        try:
            if client.test_connection():
                return jsonify({"status": "healthy", "database": "connected"}), 200
        except Exception as e:
            return jsonify({"status": "unhealthy", "error": str(e)}), 500
        
    @app.route('/api/drinkbars', methods=['GET'])
    def get_all_drinkbars():
        try:
            bars = drinkbar_service.get_all(None)
            return Response(
                json.dumps([bar.model_dump() for bar in bars]), # Uso de json.dumps para serializar una lista a cadena para que response lo interprete como bytes
                status=200,
                mimetype='application/json'
            )
        except Exception as e:
            return jsonify({"error": str(e)}), 500
        
    @app.route('/api/drinkbars/<int:limit>', methods=['GET'])
    def get_limit_drinkbars(limit):
        try:
            bars = drinkbar_service.get_all(limit)
            return Response(
                json.dumps([bar.model_dump() for bar in bars]),
                status=200,
                mimetype='application/json'
            )
        except Exception as e:
            return jsonify({"error": str(e)}), 500  
        
    @app.route('/api/drinkbars/<string:locality>', methods=['GET']) 
    def cafedrinks_por_localidad(locality : str):
        try:
            cafedrinks = drinkbar_service.get_by_locality(locality)
            return Response(
                json.dumps([cafedrink.model_dump() for cafedrink in cafedrinks]),
                status=200,
                mimetype='application/json'
            )
        except Exception as e:
            return jsonify({"error", str(e)}), 500

    @app.route('/api/drinkbars/food', methods=['GET'])
    def cafedrinks_que_sirven_comida():
        try:
            sirven_comida = drinkbar_service.get_who_serves_food()
            print(sirven_comida)
            return Response(
                json.dumps([sirve_comida.model_dump() for sirve_comida in sirven_comida]),
                status=200,
                mimetype='application/json'
            )
        except Exception as e:
            return jsonify({"error", str(e)}), 500    

    @app.route('/api/drinkbars/email/<string:email>', methods=['GET'])
    def cafedrinks_por_email(email : str):
        try:
            cafe_drinks = drinkbar_service.get_by_email(email)
            return Response(
                json.dumps([cafe_drink.model_dump() for cafe_drink in cafe_drinks]),
                status=200,
                mimetype='application/json'
            )
        except Exception as e:
            return jsonify({"error", str(e)}), 500 

    @app.route('/api/drinkbars/cercano')
    def drinkbars_cercanos():
        try:
            latitud = request.args.get('latitud') # Query Param
            longitud = request.args.get('longitud') # Query Param
        
            cercanos = drinkbar_service.get_by_cercania(latitud, longitud)
            return Response(
                json.dumps([cercano.model_dump() for cercano in cercanos]),
                status = 200,
                mimetype='application/json'
            )

        except ValueError as e:
            return jsonify({"error", "Se esperaba parámetros de consulta latitud-longitud, {e}"})
        except Exception as e:
            return jsonify({"error", str(e)})    

    @app.route('/api/drinkbars/campo')
    def drinkbars_por_campo_especifico():
        try:
            nombre_campo = request.args.get('nombre') # Query Param
            valor_campo = request.args.get('valor') # Query Param
        
            drinkbars = drinkbar_service.encontrar_por_campo(nombre_campo, valor_campo)
            return Response(
                json.dumps([drinkbar.model_dump() for drinkbar in drinkbars]),
                status = 200,
                mimetype='application/json'
            )

        except ValueError as e:
            return jsonify({"error", "Se esperaba parámetros de consulta latitud-longitud, {e}"})
        except Exception as e:
            return jsonify({"error", str(e)})             

    @app.route('/api/cafebars', methods=['GET'])
    def get_all_cafebars():
        try:
            bars = cafebar_service.get_all(None) # Le pasamos el none porque no queremos que se aplique el filtro de limitación de contenido de respuesta
            return Response(
                json.dumps([bar.model_dump() for bar in bars]),
                status=200,
                mimetype='application/json'
            )
        except Exception as e:
            return jsonify({"error": str(e)}), 500
        
    @app.route('/api/cafebars/<int:limit>', methods=['GET'])
    def get_limit_cafebars(limit):
        try:
            bars = cafebar_service.get_all(limit)
            return Response(
                json.dumps([bar.model_dump() for bar in bars]),
                status=200,
                mimetype='application/json'
            )
        except Exception as e:
            return jsonify({"error": str(e)}), 500        

    @app.route('/api/cafebars/capacity/<int:capacity>', methods=['GET'])
    def cafebars_por_capacidad(capacity : int):
        try:
            cafe_bars = cafebar_service.get_by_capacity(capacity)
            return Response(
                json.dumps([cafe_bar.model_dump() for cafe_bar in cafe_bars]),
                status = 200,
                mimetype='application/json'
            )      
        except Exception as e:
            return jsonify({"error", str(e)}), 500

    @app.route('/api/cafebars/street/<string:street>', methods=['GET'])
    def cafebars_por_calle(street: str):    
        try:
            cafe_bars = cafebar_service.get_by_street(street)
            return Response(
                json.dumps([cafe_bar.model_dump() for cafe_bar in cafe_bars]),
                status = 200,
                mimetype='application/json'
            )    
        except Exception as e :
            return jsonify({"error", str(e)}), 500
        
    @app.route('/api/cafebars/cercano/')
    def cafebars_cercanos():
        try:
            latitud = request.args.get('latitud') # Query Param
            longitud = request.args.get('longitud') # Query Param
        
            cercanos = cafebar_service.get_by_cercania(latitud, longitud)
            return Response(
                json.dumps([cercano.model_dump() for cercano in cercanos]),
                status = 200,
                mimetype='application/json'
            )

        except ValueError as e:
            return jsonify({"error", "Se esperaba parámetros de consulta latitud-longitud, {e}"})
        except Exception as e:
            return jsonify({"error", str(e)})

    @app.route('/api/cafebars/campo')
    def cafebars_por_campo_especifico():
        try:
            nombre_campo = request.args.get('nombre') # Query Param
            valor_campo = request.args.get('valor') # Query Param
        
            cafebars = cafebar_service.encontrar_por_campo(nombre_campo, valor_campo)
            return Response(
                json.dumps([cafebar.model_dump() for cafebar in cafebars]),
                status = 200,
                mimetype='application/json'
            )

        except ValueError as e:
            return jsonify({"error", "Se esperaba parámetros de consulta latitud-longitud, {e}"})
        except Exception as e:
            return jsonify({"error", str(e)})            
            
 

    @app.teardown_appcontext
    def shutdown_session(exception=None):
        close_ravendb_client()