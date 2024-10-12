pipeline {
    agent any

    environment {
        IMAGE_NAME = "trainhebrew_container_env"
        CONTAINER_NAME = "trainhebrew_container_env"
        DOCKER_PORTS = "-p 22:22 -p 10050:10050 -p 9090:9090 -p 8080:8080 -p 27017:27017"
        WEBAPP_URL = "http://localhost:8080/your-webapp/train-hebrew/process"
    }
/**    
        docker build -t trainhebrew_container_env . 
*/
    stages {
        stage('Build Docker Image') {
            steps {
                script {
                    echo "Building Docker Image..."
                    sh "docker build -t ${IMAGE_NAME} ."
                }
            }
        }
/**
        docker run -d --name trainhebrew_container_env -p 22:22 -p 10050:10050 -p 9090:9090 -p 8080:8080 -p 27017:27017 trainhebrew_container_env
        
*/
        stage('Run Docker Container') {
            steps {
                script {
                    echo "Running Docker Container..."
                    sh "docker run -d --name ${CONTAINER_NAME} ${DOCKER_PORTS} ${IMAGE_NAME}"
                }
            }
        }
/**     
        #onlyremote docker exec -i trainhebrew_container_env hostname -i | xargs -I {} ssh-keyscan -H {} >> ~/.ssh/known_hosts
        #onlyremote docker exec -i trainhebrew_container_env hostname -i | xargs -I {} printf "[docker_containers]\ntrainhebrew_container_env ansible_host={} ansible_user=ansible_user ansible_ssh_pass=ansible_password ansible_ssh_private_key_file=~/.ssh/id_rsa\n" > /ansible/inventory.ini
        cd ansible; ansible -i inventory.ini trainhebrew_container_env -m ping
*/
        stage('Ping Ansible Target') {
            steps {
                dir('ansible') {
                    script {
                        echo "Pinging Container with Ansible..."
                        sh "ansible -i inventory.ini ${CONTAINER_NAME} -m ping"
                    }
                }
            }
        }

        stage('Run Ansible Playbook') {
            steps {
                dir('ansible') {
                    script {
                        echo "Running Ansible Playbook..."
                        sh "ansible-playbook -i inventory.ini setup-audio-processing.yml"
                    }
                }
            }
        }

        stage('Run Docker with Exposed Ports') {
            steps {
                script {
                    echo "Running Docker Container with Ports..."
                    sh "docker run -d ${DOCKER_PORTS} ${IMAGE_NAME}"
                }
            }
        }

        stage('Invoke Web API') {
            steps {
                script {
                    echo "Invoking Web API..."
                    def requestData = '''
                    {
                        "train_dir": "/data/train",
                        "lang_dir": "/data/lang",
                        "model_dir": "/opt/kaldi/exp/tri2",
                        "praat_script": "/opt/praat/voice_isolation.praat",
                        "mongo_uri": "mongodb://localhost:27017",
                        "db_name": "HebrewSpeechRecognition",
                        "collection_name": "Transcriptions"
                    }
                    '''
                    sh "curl -X POST ${WEBAPP_URL} -H 'Content-Type: application/json' -d '${requestData}'"
                }
            }
        }
    }
}