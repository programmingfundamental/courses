"""A passing but insufficient test; TODO: add invalid and boundary cases."""
from ai_platform.api.app import Features


def test_happy_path_only():
    row = Features(token_count=100, keyword_score=0.5, previous_requests=1)
    assert row.token_count == 100
