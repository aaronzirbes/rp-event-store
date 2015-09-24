#!/bin/bash

# using httpie rather than curl

http post :8080/events < src/test/resources/fixtures/purchaseBikeEvent.json
http post :8080/events < src/test/resources/fixtures/adjustSeatHeightEvent.json
http post :8080/events < src/test/resources/fixtures/flatTireEvent.json
http post :8080/events < src/test/resources/fixtures/inflateTireEvent.json
http post :8080/events < src/test/resources/fixtures/lockBikeEvent.json
http post :8080/events < src/test/resources/fixtures/repairFlatEvent.json
http post :8080/events < src/test/resources/fixtures/rideBikeEvent.json
http post :8080/events < src/test/resources/fixtures/turnOffLightEvent.json
http post :8080/events < src/test/resources/fixtures/turnOnLightEvent.json
http post :8080/events < src/test/resources/fixtures/unockBikeEvent.json
http post :8080/events < src/test/resources/fixtures/repairFrontFlatEvent.json
http post :8080/events < src/test/resources/fixtures/inflateFrontTireEvent.json

http get :8080/events/dd4cad36-85be-48b3-b2e1-51cc93712e4c
http get :8080/aggregate/dd4cad36-85be-48b3-b2e1-51cc93712e4c

http get docker:9200/_search?q=make:Surly

