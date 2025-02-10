FROM python:3.7-slim

# 작업 디렉터리 설정
WORKDIR /app

# Git 설치 및 레포지토리 클론
RUN apt-get update && \
    apt-get install -y git cron && \
    git clone https://github.com/ahachulTeam/ahachul_data.git /app/ahachul_data && \
    apt-get clean

# requirements.txt 복사 및 Python 패키지 설치
RUN pip install --no-cache-dir -r /app/ahachul_data/requirements.txt

# 스크립트 복사 및 실행 권한 부여
RUN mkdir -p /app/script && \
    echo '#!/bin/bash\npython /app/ahachul_data/main.py -o un' > /app/script/new.sh && \
    chmod +x /app/script/new.sh \

# 크론탭 설정
RUN echo "0 15 * * * /app/script/new.sh >> /var/log/cron.log 2>&1" > /etc/cron.d/new-task
RUN chmod 0644 /etc/cron.d/new-task

# 크론 데몬 실행
CMD ["cron", "-f"]
