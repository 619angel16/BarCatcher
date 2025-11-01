import os
from dotenv import load_dotenv

load_dotenv()

class Config:
    """Base config"""
    # Database
    SQLALCHEMY_DATABASE_URI = os.getenv("DATABASE_URL")
    SQLALCHEMY_TRACK_MODIFICATIONS = False

    SERVICE_NAME = "BarCatcher"
    SERVICE_VERSION = "1.0.0"

class DevelopmentConfig(Config):
    """Developement Config"""
    DEBUG = True
    TESTING = False

class ProductionConfig(Config):
    """Production Config"""
    DEBUG = False
    TESTING = False

class TestingConfig(Config):
    """Testing Config"""
    DEBUG = True
    TESTING = True
    SQLALCHEMY_DATABASE_URI = os.getenv("TEST_DATABASE_URL", "sqlite:///:memory:")

# Config mapping
config = {
    'development': DevelopmentConfig,
    'production': ProductionConfig,
    'testing': TestingConfig,
    'default': DevelopmentConfig
}