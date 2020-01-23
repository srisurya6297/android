#!/usr/bin/env bash

## emulator
if ( [[ $(emulator -list-avds | grep uiComparison -c) -eq 0 ]] ); then
    (sleep 5; echo "no") | avdmanager create avd -n uiComparison -c 100M -k "system-images;android-27;google_apis;x86" --abi "google_apis/x86"
fi

emulator -avd uiComparison -no-snapshot -gpu swiftshader_indirect -no-window -no-audio -skin 500x833 1>/dev/null &
PID=$(echo $!)

## server
docker run --name=uiComparison nextcloudci/server:server-13 1>/dev/null &
sleep 5
IP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' uiComparison)

if [[ $IP = "" ]]; then
    echo "no server"
    exit 1
fi

## run on server
cp gradle.properties gradle.properties_
sed -i s"/server/$IP/" gradle.properties
scripts/wait_for_emulator.sh

# setup test server
docker exec uiComparison /bin/sh -c "/initnc.sh"
docker exec uiComparison /bin/sh -c "su www-data -c \"OC_PASS=user1 php /var/www/html/occ user:add --password-from-env --display-name='User One' user1\""
docker exec uiComparison /bin/sh -c "su www-data -c \"OC_PASS=user2 php /var/www/html/occ user:add --password-from-env --display-name='User Two' user2\""
docker exec uiComparison /bin/sh -c "su www-data -c \"OC_PASS=user3 php /var/www/html/occ user:add --password-from-env --display-name='User Three' user3\""
docker exec uiComparison /bin/sh -c "su www-data -c \"php /var/www/html/occ user:setting user2 files quota 1G\""
docker exec uiComparison /bin/sh -c "su www-data -c \"php /var/www/html/occ group:add users\""
docker exec uiComparison /bin/sh -c "su www-data -c \"php /var/www/html/occ group:adduser users user1\""
docker exec uiComparison /bin/sh -c "su www-data -c \"php /var/www/html/occ group:adduser users user2\""
docker exec uiComparison /bin/sh -c "/run.sh"

## update/create all screenshots
./gradlew executeScreenshotTests -Precord

## update/create only one test
#./gradlew executeScreenshotTests -Precord -Pandroid.testInstrumentationRunnerArguments.class=com.nextcloud.client.FileDisplayActivityIT#showShares

mv gradle.properties_ gradle.properties

# tidy up
kill $PID
docker stop uiComparison
docker rm uiComparison
