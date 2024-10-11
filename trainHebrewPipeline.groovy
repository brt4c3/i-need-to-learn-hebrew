pipeline {
    agent any

    environment {
        IMAGE_NAME = "trainhebrew_container_ubuntu_env"
        CONTAINER_NAME = "trainhebrew_container_ubuntu_env"
        DOCKER_PORTS = "-p 22:22 -p 10050:10050 -p 9090:9090 -p 8080:8080 -p 27017:27017"
        WEBAPP_URL = "http://localhost:8080/your-webapp/train-hebrew/process"
    }

    stages {
        stage('Build Docker Image') {
            steps {
                script {
                    echo "Building Docker Image..."
                    sh "docker build -t ${IMAGE_NAME} ."
                }
            }
        }

        stage('Run Docker Container') {
            steps {
                script {
                    echo "Running Docker Container..."
                    sh "docker run -d --name ${CONTAINER_NAME} ${DOCKER_PORTS} ${IMAGE_NAME}"
                }
            }
        }

        stage('Ping Ansible Target') {
            steps {
                dir('ansible') {
                    script {
                        echo "Pinging Container with Ansible..."
                        sh "ansible -m ping ${CONTAINER_NAME}"
                    }
                }
            }
        }

        stage('Run Ansible Playbook') {
            steps {
                dir('ansible') {
                    script {
                        echo "Running Ansible Playbook..."
                        sh "ansible-playbook -K setup-audio-processing.yml"
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
                        "train_dir": "/path/to/train",
                        "lang_dir": "/path/to/lang",
                        "model_dir": "/path/to/model",
                        "praat_script": "/path/to/script",
                        "mongo_uri": "mongodb://localhost:27017",
                        "db_name": "audioDB",
                        "collection_name": "transcriptions"
                    }
                    '''
                    sh "curl -X POST ${WEBAPP_URL} -H 'Content-Type: application/json' -d '${requestData}'"
                }
            }
        }
    }
}