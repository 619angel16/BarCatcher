from flask import jsonify
from datetime import datetime
from helpers.connectBar_CC import get_ravendb_client, close_ravendb_client
from repository.CafebarsRepository import CafebarsRepository
from repository.CafedrinksRepository import CafedrinksRepository
from service.CafebarsService import CafebarsService
from service.CafedrinksService import CafedrinksService
from typing import Optional

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
                "drinkbars": "/api/drinkbars"
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
            return jsonify([bar.model_dump() for bar in bars]), 200
        except Exception as e:
            return jsonify({"error": str(e)}), 500
        
    @app.route('/api/drinkbars/<int:limit>', methods=['GET'])
    def get_limit_drinkbars(limit):
        try:
            bars = drinkbar_service.get_all(limit)
            return jsonify([bar.model_dump() for bar in bars]), 200
        except Exception as e:
            return jsonify({"error": str(e)}), 500  

    @app.route('/api/cafebars', methods=['GET'])
    def get_all_cafebars():
        try:
            bars = cafebar_service.get_all(None)
            return jsonify([bar.model_dump() for bar in bars]), 200
        except Exception as e:
            return jsonify({"error": str(e)}), 500
        
    @app.route('/api/cafebars/<int:limit>', methods=['GET'])
    def get_limit_cafebars(limit):
        try:
            bars = cafebar_service.get_all(limit)
            return jsonify([bar.model_dump() for bar in bars]), 200
        except Exception as e:
            return jsonify({"error": str(e)}), 500        
            

    @app.teardown_appcontext
    def shutdown_session(exception=None):
        close_ravendb_client()