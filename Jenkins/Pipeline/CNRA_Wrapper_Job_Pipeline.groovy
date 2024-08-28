def DROP, ENM_ISO_VERSION, TEST_PHASE, CENTRAL_CSV_VERSION, MT_CSV_FILE_URI, TEARDOWN_PRODUCT_SET_JSON, TEARDOWN_PRODUCT_SET_VERSION

pipeline {
   agent{
       node{
           label CNRA_Wrapper_Job_Agents
       }
   }
   options {
       timestamps()
	disableConcurrentBuilds() 
   }
   stages {
       stage('Get latest green product set for teardown') {
           when {
               expression { params.job_type != 'upgrade' && params.job_type != 'taf_only' }
           }
           steps {
               script {
                   TEARDOWN_PRODUCT_SET_JSON = sh(script: 'curl --location --request GET \'https://ci-portal.seli.wh.rnd.internal.ericsson.com/api/cloudNative/getConfidenceLevelVersion/\'', returnStdout: true).trim()
                   def json = new groovy.json.JsonSlurper().parseText(TEARDOWN_PRODUCT_SET_JSON)
                   TEARDOWN_PRODUCT_SET_VERSION = json."cENM-Deploy-II-CSAR-Lite"
               }
           }
       }
       stage('Get Additional Parameters for Pipeline') {
           when {
               expression { params.job_type != 'teardown' }
           }
           steps {
               script {
                   TEST_PHASE = "MTE"
                   drop_values = String.valueOf(product_set_version).tokenize('.')
                   DROP = drop_values[0] + "." + drop_values[1]
                   echo "Drop is " + "$DROP"
                   product_set = String.valueOf(product_set_version).split('-')[0]
                   echo "Product Set is " + "$product_set"
                   curl_response = sh(script:'curl -L -s https://ci-portal.seli.wh.rnd.internal.ericsson.com/api/deployment/deploymentutilities/productSet/ENM/version/' + "$product_set", returnStdout: true)
                   ENM_ISO_VERSION = curl_response.substring(curl_response.lastIndexOf("mediaArtifactVersion")).split('"')[2]
                   echo "ENM ISO Version is " + "$ENM_ISO_VERSION"
                   CENTRAL_CSV_VERSION = curl_response.substring(curl_response.lastIndexOf("centralCSVVersion")).split('"')[2]
                   echo "CENTRAL CSV VERSION is " + "$CENTRAL_CSV_VERSION"
                   if(String.valueOf(mt_csv_file_uri) == "") {
                       MT_CSV_FILE_URI = "https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/content/sites/tor/enm-maintrack-central-test-datasource/${CENTRAL_CSV_VERSION}/maintrack/csv/nodeToAdd_2K.csv"
                   } else {
                       MT_CSV_FILE_URI = String.valueOf(mt_csv_file_uri)
                   }
               }
           }
       }
      stage('Install, Install Only, Upgrade, or Teardown') {
          parallel {
              stage('Teardown, Install, Add Licenses, Update Trust Profiles') {
                  when {
                      expression { params.job_type == 'install' }
                  }
                  steps {
                      build job: 'cENM_Design_Teams_Uninstall', parameters: [
                          string(name: 'Artifact_type', value: 'csar_lite'),
                          string(name: 'use_case', value: 'Teardown'),
                          string(name: 'product_set_version', value: "$TEARDOWN_PRODUCT_SET_VERSION"),
                          string(name: 'deployment_size', value: String.valueOf(deployment_size)),
                          string(name: 'orchestration_type', value: 'helm'),
                          string(name: 'container_registry_type', value: 'other'),
                          string(name: 'snapshot_csar_lite_location', value: String.valueOf(snapshot_csar_lite_location)),
                          string(name: 'snapshot_integration_key_value_pairs', value: String.valueOf(snapshot_integration_key_value_pairs)),
                          string(name: 'backup_name', value: ''),
                          string(name: 'scope', value: 'ROLLBACK'),
                          string(name: 'BRO_DEFAULT_BACKUP_TYPE', value: 'Internal'),
                          string(name: 'environment_name', value: String.valueOf(environment_name))
                      ]
                      build job: 'cENM_Design_Teams_Install', parameters: [
                          string(name: 'Artifact_type', value: String.valueOf(Artifact_type)),
                          string(name: 'use_case', value: 'Install'),
                          string(name: 'product_set_version', value: String.valueOf(product_set_version)),
                          string(name: 'deployment_size', value: String.valueOf(deployment_size)),
                          string(name: 'orchestration_type', value: 'helm'),
                          string(name: 'container_registry_type', value: 'other'),
                          string(name: 'snapshot_csar_lite_location', value: String.valueOf(snapshot_csar_lite_location)),
                          string(name: 'snapshot_integration_key_value_pairs', value: String.valueOf(snapshot_integration_key_value_pairs)),
                          string(name: 'backup_name', value: ''),
                          string(name: 'scope', value: 'ROLLBACK'),
                          string(name: 'BRO_DEFAULT_BACKUP_TYPE', value: 'Internal'),
                          string(name: 'environment_name', value: String.valueOf(environment_name))
                      ]
                      build job: 'cENM_Add_Licenses', parameters: [
                          string(name: 'test_phase', value: "$TEST_PHASE"),
                          string(name: 'cluster_id', value: String.valueOf(environment_name)),
                          string(name: 'mt_utils_version', value: String.valueOf(mt_utils_version)),
                          string(name: 'drop', value: "$DROP"),
                          string(name: 'product_set_version', value: String.valueOf(product_set_version)),
                          string(name: 'deployment_type', value: 'cloud')
                      ]
                      build job: 'cENM_Update_Trust_Profile', parameters: [
                          string(name: 'clusterId', value: String.valueOf(environment_name)),
                          string(name: 'drop', value: "$DROP"),
                          string(name: 'simdep_release', value: String.valueOf(simdep_release)),
                          string(name: 'nodesCleanUp', value: String.valueOf(nodesCleanUp)),
                          string(name: 'deployment_type', value: 'Cloud'),
                          string(name: 'MT_utils_version', value: String.valueOf(mt_utils_version))
                      ]
                  }
              }
              stage('Install Only - Teardown, Install') {
                  when {
                      expression { params.job_type == 'install_only' }
                  }
                  steps {
                      build job: 'cENM_Design_Teams_Uninstall', parameters: [
                          string(name: 'Artifact_type', value: 'csar_lite'),
                          string(name: 'use_case', value: 'Teardown'),
                          string(name: 'product_set_version', value: "$TEARDOWN_PRODUCT_SET_VERSION"),
                          string(name: 'deployment_size', value: String.valueOf(deployment_size)),
                          string(name: 'orchestration_type', value: 'helm'),
                          string(name: 'container_registry_type', value: 'other'),
                          string(name: 'snapshot_csar_lite_location', value: String.valueOf(snapshot_csar_lite_location)),
                          string(name: 'snapshot_integration_key_value_pairs', value: String.valueOf(snapshot_integration_key_value_pairs)),
                          string(name: 'backup_name', value: ''),
                          string(name: 'scope', value: 'ROLLBACK'),
                          string(name: 'BRO_DEFAULT_BACKUP_TYPE', value: 'Internal'),
                          string(name: 'environment_name', value: String.valueOf(environment_name))
                      ]
                      build job: 'cENM_Design_Teams_Install', parameters: [
                          string(name: 'Artifact_type', value: String.valueOf(Artifact_type)),
                          string(name: 'use_case', value: 'Install'),
                          string(name: 'product_set_version', value: String.valueOf(product_set_version)),
                          string(name: 'deployment_size', value: String.valueOf(deployment_size)),
                          string(name: 'orchestration_type', value: 'helm'),
                          string(name: 'container_registry_type', value: 'other'),
                          string(name: 'snapshot_csar_lite_location', value: String.valueOf(snapshot_csar_lite_location)),
                          string(name: 'snapshot_integration_key_value_pairs', value: String.valueOf(snapshot_integration_key_value_pairs)),
                          string(name: 'backup_name', value: ''),
                          string(name: 'scope', value: 'ROLLBACK'),
                          string(name: 'BRO_DEFAULT_BACKUP_TYPE', value: 'Internal'),
                          string(name: 'environment_name', value: String.valueOf(environment_name))
                      ]
                  }
              }
              stage('Upgrade') {
                  when {
                      expression { params.job_type == 'upgrade' }
                  }
                  steps {
                      build job: 'cENM_Design_Teams_Upgrade', parameters: [
                          string(name: 'Artifact_type', value: String.valueOf(Artifact_type)),
                          string(name: 'use_case', value: 'Upgrade'),
                          string(name: 'product_set_version', value: String.valueOf(product_set_version)),
                          string(name: 'deployment_size', value: String.valueOf(deployment_size)),
                          string(name: 'orchestration_type', value: 'helm'),
                          string(name: 'container_registry_type', value: 'other'),
                          string(name: 'snapshot_csar_lite_location', value: String.valueOf(snapshot_csar_lite_location)),
                          string(name: 'snapshot_integration_key_value_pairs', value: String.valueOf(snapshot_integration_key_value_pairs)),
                          string(name: 'backup_name', value: String.valueOf(backup_name)),
                          string(name: 'scope', value: 'ROLLBACK'),
                          string(name: 'BRO_DEFAULT_BACKUP_TYPE', value: 'Internal'),
                          string(name: 'environment_name', value: String.valueOf(environment_name))
                      ]
                  }
              }
              stage('Teardown') {
                  when {
                      expression { params.job_type == 'teardown' }
                  }
                  steps {
                      build job: 'cENM_Design_Teams_Uninstall', parameters: [
                          string(name: 'Artifact_type', value: 'csar_lite'),
                          string(name: 'use_case', value: 'Teardown'),
                          string(name: 'product_set_version', value: "$TEARDOWN_PRODUCT_SET_VERSION"),
                          string(name: 'deployment_size', value: String.valueOf(deployment_size)),
                          string(name: 'orchestration_type', value: 'helm'),
                          string(name: 'container_registry_type', value: 'other'),
                          string(name: 'snapshot_csar_lite_location', value: String.valueOf(snapshot_csar_lite_location)),
                          string(name: 'snapshot_integration_key_value_pairs', value: String.valueOf(snapshot_integration_key_value_pairs)),
                          string(name: 'backup_name', value: ''),
                          string(name: 'scope', value: 'ROLLBACK'),
                          string(name: 'BRO_DEFAULT_BACKUP_TYPE', value: 'Internal'),
                          string(name: 'environment_name', value: String.valueOf(environment_name))
                      ]
                  }
              }
          }
      }
      stage('Run TAF') {
          parallel {
              stage('Post Install TAF') {
                  when {
                      expression { params.job_type == 'install' && params.run_taf }
                  }
                  steps {
                      build job: 'cENM_Design_Teams_TAF', parameters: [
                          string(name: 'cluster_id', value: String.valueOf(environment_name)),
                          string(name: 'testware_items', value: String.valueOf(testware_items)),
                          string(name: 'internal_nodes', value: String.valueOf(internal_nodes)),
                          string(name: 'MT_CSV_FILE_URI', value: "$MT_CSV_FILE_URI")
                      ]
                  }
              }
              stage('Post Install Only TAF') {
                  when {
                      expression { params.job_type == 'install_only' && params.run_taf }
                  }
                  steps {
                      build job: 'cENM_Design_Teams_TAF', parameters: [
                          string(name: 'cluster_id', value: String.valueOf(environment_name)),
                          string(name: 'testware_items', value: String.valueOf(testware_items)),
                          string(name: 'internal_nodes', value: String.valueOf(internal_nodes)),
                          string(name: 'MT_CSV_FILE_URI', value: "$MT_CSV_FILE_URI")
                      ]
                  }
              }
              stage('Post Upgrade TAF') {
                  when {
                      expression { params.job_type == 'upgrade' && params.run_taf }
                  }
                  steps {
                      build job: 'cENM_Design_Teams_TAF', parameters: [
                          string(name: 'cluster_id', value: String.valueOf(environment_name)),
                          string(name: 'testware_items', value: String.valueOf(testware_items)),
                          string(name: 'internal_nodes', value: String.valueOf(internal_nodes)),
                          string(name: 'MT_CSV_FILE_URI', value: "$MT_CSV_FILE_URI")
                      ]
                  }
              }
              stage('TAF Only') {
                  when {
                      expression { params.job_type == 'taf_only'}
                  }
                  steps{
                      build job: 'cENM_Design_Teams_TAF', parameters: [
                          string(name: 'cluster_id', value: String.valueOf(environment_name)),
                          string(name: 'testware_items', value: String.valueOf(testware_items)),
                          string(name: 'internal_nodes', value: String.valueOf(internal_nodes)),
                          string(name: 'MT_CSV_FILE_URI', value: "$MT_CSV_FILE_URI")
                      ]
                  }
              }
          }
      }
   }
}
