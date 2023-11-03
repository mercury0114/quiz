set -e

manifest_path=~/Desktop/git/vocabulary/app/src/main/AndroidManifest.xml
manifest_activities=$(cat $manifest_path | grep -o '\..*Activity' | cut -c2-)
echo $manifest_activities | xargs -n1 | sort | xargs >/tmp/manifest_activities.txt

java_activities=$(find ~/Desktop/git/vocabulary -type f \
                    | grep -o '.*Activity\.java' \
                    | xargs -l basename | rev | cut -c6- | rev)
echo $java_activities | xargs -n1 | sort | xargs >/tmp/java_activities.txt

echo "Checking if all *Activity.java classes are listed in app/src/main/AndroidManifest.xml"
cmp /tmp/manifest_activities.txt /tmp/java_activities.txt
