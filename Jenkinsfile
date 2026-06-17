pipeline {
    agent any
    triggers {
        pollSCM('*/5 * * * *')
    }
    stages {
        stage('Checkout') {
            steps { checkout scm }
        }
        stage('Build') {
            environment {
                JAVA_HOME = 'C:\\Program Files\\Eclipse Adoptium\\jdk-21.0.9.10-hotspot'
            }
            steps {
                bat './mvnw.cmd clean package -DskipTests -Dmaven.compiler.release=21'
            }
        }
        stage('Test') {
            environment {
                JAVA_HOME = 'C:\\Program Files\\Eclipse Adoptium\\jdk-21.0.9.10-hotspot'
            }
            steps {
                bat './mvnw.cmd test -Dmaven.compiler.release=21'
            }
        }
        stage('Deploy to Web Server') {
            when { branch 'main' }
            steps {
                bat 'wsl ansible-playbook -i hosts.ini deploy.yml'
            }
        }
    }
    post {
        failure {
            emailext(
                to: 'srengty@gmail.com',
                recipientProviders: [[$class: 'CulpritsRecipientProvider']],
                subject: "BUILD FAILED: ${env.JOB_NAME} - #${env.BUILD_NUMBER}",
                body: """
                    <h2>Build Failed</h2>
                    <p><b>Project:</b> ${env.JOB_NAME}</p>
                    <p><b>Build #:</b> ${env.BUILD_NUMBER}</p>
                    <p><b>URL:</b> <a href='${env.BUILD_URL}'>${env.BUILD_URL}</a></p>
                    <p><b>Committed by:</b> ${env.CHANGE_AUTHOR_EMAIL}</p>
                    <hr/>
                    <p>Check console output for details.</p>
                """
            )
        }
    }
}