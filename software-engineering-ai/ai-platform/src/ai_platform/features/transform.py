import pandas as pd
from ai_platform.data.dataset import FEATURES


def feature_frame(frame: pd.DataFrame) -> pd.DataFrame:
    # Cohort and label are deliberately excluded from model input.
    return frame.loc[:, FEATURES].copy()
