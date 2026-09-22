from pathlib import Path
from pydantic import Field, SecretStr
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_prefix="AI_", extra="ignore")
    artifact_root: Path = Path("artifacts")
    model_version: str = Field(default="demo-v1", pattern=r"^[a-zA-Z0-9][a-zA-Z0-9._-]{0,63}$")
    api_key: SecretStr = SecretStr("")
    max_batch: int = Field(default=32, ge=1, le=128)
