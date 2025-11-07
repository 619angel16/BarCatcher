import requests
from typing import Optional
from requests_pkcs12 import Pkcs12Adapter
from dotenv import load_dotenv
import os

load_dotenv()

class RavenDBClient:
    """
    Cliente directo para RavenDB usando la HTTP API
    """
    
    def __init__(self, server_url, database_name, cert_path, cert_password):
        self.server_url = server_url.rstrip('/')
        self.database_name = database_name
        self.cert_path = cert_path
        self.cert_password = cert_password
        
        # Crear sesión con certificado
        self.session = requests.Session()
        self.session.mount('https://', Pkcs12Adapter(
            pkcs12_filename=cert_path,
            pkcs12_password=cert_password
        ))
        
        self.base_url = f"{self.server_url}/databases/{self.database_name}"
        
    def test_connection(self) -> bool:
        """Probar la conexión"""
        try:
            response = self.session.get(f"{self.base_url}/docs")
            if response.status_code == 200:
                print("✅ Conexión exitosa con certificado")
                return True
            else:
                print(f"❌ Error: {response.status_code} - {response.text}")
                return False
        except Exception as e:
            print(f"❌ Error de conexión: {e}")
            return False
    
    def get_document(self, doc_id: str) -> Optional[dict]:
        """Obtener un documento por ID"""
        try:
            response = self.session.get(f"{self.base_url}/docs?id={doc_id}")
            if response.status_code == 200:
                result = response.json()

                if 'Result' in result and len(result['Result']) > 0:
                    return result['Result'][0] # Obtenemos los datos almacenados en el campo Result.
                return result
            else:
                print(f"❌ Error obteniendo documento: {response.status_code}")
                return None
        except Exception as e:
            print(f"❌ Error: {e}")
            return None
    
    def query_documents(self, query=None):
        """Consultar documentos"""
        try:
            url = f"{self.base_url}/queries"
            payload = {
                "Query": query if query else "FROM @all_docs"
            }
            
            response = self.session.post(url, json=payload)
            if response.status_code == 200:
                return response.json()
            else:
                print(f"❌ Error en query: {response.status_code} - {response.text}")
                return None
        except Exception as e:
            print(f"❌ Error: {e}")
            return None
        
    def save_document(self, doc_id : str, document : dict) -> bool:
        try:
            url = f"{self.base_url}/docs?id={doc_id}" 
            response = self.session.put(url = url, data = document)   
            return response.status_code in [200, 201]
        except Exception as e:
            print(f"❌ Error al insertar {doc_id}: {e}")
            return False
        
    def delete_document(self, entity_id: str) -> bool:
        try:
            url = f"{self.base_url}/docs?id={entity_id}"
            response = self.session.delete(url)
            return response.status_code == 204
        except Exception as e:
            print(f"❌ Error al eliminar {entity_id}: {e}")
            return False



    
    
# Singleton de cliente RavenDB
_client: Optional[RavenDBClient] = None

def get_ravendb_client() -> RavenDBClient:
    global _client
    
    _client=  RavenDBClient(
        server_url=os.getenv("SERVER_URL"),
        database_name=os.getenv("DATABASE_BARS_NAME"),
        cert_path=os.getenv("CERT_PATH"),
        cert_password=os.getenv("CERT_PASSWORD")
    )

    if not _client.test_connection():
        raise ConnectionError("No se pudo conectar a RavenDB")
    
    return _client

def close_ravendb_client():
    global _client

    if _client is not None:
        _client.session.close()
        _client = None