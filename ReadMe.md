# i-need-to-learn-hebrew
# Project : i-need-to-learn-hebrew
# Date : 2024-10-05
# Description : I need to learn hebrew but dont have the energy for it.
# OS : Debian 
# arch : x86_64

# Diclaimer 
i have huge amount of bug 
considering to move jenkins to docker

# Convert p4 to wav
ffmpeg -i 'LilTexas.mp3' -vn -acodec pcm_s16le -ar 44100 -ac 2 output.wav


## Logic Flow 
<!--   1. Recording
        - Pulse Audio for raspie only
        // forget about this part
-->
    2. Preparation 
    3. Convert to Text
    4. Convert to Sentence
    5. Translate

# CI/CD

## Docker 
<!-- 
    - "docker buildx create --use"
    - "export DOCKER_BUILDKIT=1"
    // forget about raspie for now
-->
## Ansible 
    - prepare the files
## Build
    - just use maven locally
## Automation
    - Groovy
    // Having problems running groovy pipeline loaclly


