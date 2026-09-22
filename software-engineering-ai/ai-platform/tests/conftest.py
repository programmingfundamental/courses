from pathlib import Path
import pytest
from ai_platform.training.pipeline import train


@pytest.fixture(scope="session")
def data_path():
    return Path(__file__).parents[1] / "data" / "requests-v1.csv"


@pytest.fixture(scope="session")
def trained(tmp_path_factory, data_path):
    root = tmp_path_factory.mktemp("registry")
    metadata = train(data_path, root, "test-v1")
    return root, metadata
