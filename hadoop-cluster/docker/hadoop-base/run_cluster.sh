RUN docker build -t hadoop-base
	
WORKDIR ../docker-compose

RUN docker-compose up -d
