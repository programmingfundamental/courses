FROM python:3.12.14-slim
WORKDIR /app
ENV PYTHONDONTWRITEBYTECODE=1 PYTHONUNBUFFERED=1
COPY requirements.lock pyproject.toml ./
RUN pip install --no-cache-dir -r requirements.lock
COPY src src
RUN pip install --no-cache-dir --no-deps --no-build-isolation .
# The release bundle is trained and validated before build, never at API startup.
ARG MODEL_VERSION=demo-v1
COPY artifacts/${MODEL_VERSION}/ /app/artifacts/${MODEL_VERSION}/
ENV AI_MODEL_VERSION=${MODEL_VERSION} AI_ARTIFACT_ROOT=/app/artifacts
USER 10001:10001
EXPOSE 8000
CMD ["uvicorn","ai_platform.api.app:app","--host","0.0.0.0","--port","8000","--no-access-log"]
