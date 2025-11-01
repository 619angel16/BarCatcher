from flask import jsonify
from datetime import datetime

def register_blueprints(app):
    """Registry all app's blueprints"""
    # En el controller se debe de definir la blueprint (path de la petición que debe de registrar Flask)
    # Más info -> Buscar en google por Blueprints Flask
    #from controller.XXXX import nombre_blueprint
    
    # Registry endpoints paths
    #app.register_blueprint(nombre_blueprint, url_prefix='/api/v1')
    
    # Registry basic paths
    register_basic_routes(app)


def register_basic_routes(app):
    """Registra basic app's paths"""
    
    @app.route('/')
    def index():
        return jsonify({
            "service": app.config['SERVICE_NAME'],
            "version": app.config['SERVICE_VERSION'],
            "status": "running",
            "endpoints-generales": {
                "health": "/api/v1/health"
            }
        })
    
    @app.route('/api/v1/health')
    def health_check():
        return jsonify({
            "status": "healthy",
            "service": app.config['SERVICE_NAME'],
            "timestamp": datetime.today()
        })