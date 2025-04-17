pipeline {
    agent {
        docker {
            image 'maven:3-openjdk-18'
            args '-v /var/jenkins_home/.m2:/root/.m2'
        }
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        // stage('Archive Artifacts') {
        //     steps {
        //         // Archive artifacts in Jenkins
        //         archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        //     }
        // }
        
        // stage('Upload to Artifact Repository') {
        //     steps {
        //         script {
        //             // Example for uploading to Artifactory/JFrog
        //             def server = Artifactory.server 'artifactory-server'
        //             def uploadSpec = """{
        //                 "files": [
        //                     {
        //                         "pattern": "target/*.jar",
        //                         "target": "my-repo/${ARTIFACT_NAME}/${VERSION}/",
        //                         "props": "quality=production;status=released"
        //                     }
        //                 ]
        //             }"""
                    
        //             server.upload spec: uploadSpec
                    
        //             // Alternatively, for Nexus Repository
        //             // sh "curl -v -u ${NEXUS_CREDS_USR}:${NEXUS_CREDS_PSW} --upload-file target/*.jar https://nexus.example.com/repository/my-repo/${ARTIFACT_NAME}/${VERSION}/"
                    
        //             // For Amazon S3
        //             // withAWS(region: 'us-east-1', credentials: 'aws-credentials') {
        //             //     s3Upload(file: 'target/*.jar', bucket: 'my-artifact-bucket', path: "${ARTIFACT_NAME}/${VERSION}/")
        //             // }
        //         }
        //     }
        // }
    }
    
    post {
        success {
            echo "Build successful! Artifacts uploaded to repository."
        }
        failure {
            echo "Build failed. Check logs for details."
        }
        always {
            // Clean workspace
            cleanWs()
        }
    }
}