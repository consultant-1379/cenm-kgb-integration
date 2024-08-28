job('CNRA_Views_Rollout'){
  label("${CNRA_Wrapper_Job_Agents}")
  concurrentBuild(allowConcurrentBuild = true)
  logRotator {
    numToKeep(30)
  }
  scm {
        git {
          branch('master')
          remote {
            credentials('lciadm100_private_key')
            url("${GERRIT_MIRROR}/OSS/ENM-Parent/SQ-Gate/com.ericsson.de/cenm-kgb-integration")
          }
          extensions {
            cleanBeforeCheckout()
          }
        }
      }
  steps {
      dsl {
        text(readFileFromWorkspace('Jenkins/JobDSL/CNRA_Views_Rollout.groovy'))
    }
  }
}
job('CNRA_cENM_Pipeline_Jobs_Rollout'){
  label("${CNRA_Wrapper_Job_Agents}")
  concurrentBuild(allowConcurrentBuild = true)
  logRotator {
    numToKeep(30)
  }
  parameters {
    stringParam('CNRA_Pipeline_Build_Agents', "${CNRA_Pipeline_Build_Agents}",'The agents configured to run the wrapper jobs')
  }
  scm {
        git {
          branch('master')
          remote {
            credentials('lciadm100_private_key')
            url("${GERRIT_MIRROR}/OSS/ENM-Parent/SQ-Gate/com.ericsson.de/cenm-kgb-integration")
          }
          extensions {
            cleanBeforeCheckout()
          }
        }
      }
  steps {
      dsl {
        text(readFileFromWorkspace('Jenkins/JobDSL/CNRA_cENM_Pipeline_Jobs_Rollout.groovy'))
    }
  }
}
job('CNRA_TAF_Job_Rollout'){
  label("${CNRA_Wrapper_Job_Agents}")
  concurrentBuild(allowConcurrentBuild = true)
  logRotator {
    numToKeep(30)
  }
  parameters {
    stringParam('CNRA_TE_Docker_Agents', "${CNRA_TE_Docker_Agents}",'The agents configured to run the TE jobs')
  }
  scm {
        git {
          branch('master')
          remote {
            credentials('lciadm100_private_key')
            url("${GERRIT_MIRROR}/OSS/ENM-Parent/SQ-Gate/com.ericsson.de/cenm-kgb-integration")
          }
          extensions {
            cleanBeforeCheckout()
          }
        }
      }
  steps {
      dsl {
        text(readFileFromWorkspace('Jenkins/JobDSL/CNRA_TAF_Job_Rollout.groovy'))
    }
  }
}
job('CNRA_Wrapper_Jobs_Rollout'){
  label("${CNRA_Wrapper_Job_Agents}")
  concurrentBuild(allowConcurrentBuild = true)
  logRotator {
    numToKeep(30)
  }
  parameters {
    stringParam('CNRA_Wrapper_Job_Agents', "${CNRA_Wrapper_Job_Agents}",'The agents configured to run the Wrapper jobs')
  }
  scm {
        git {
          branch('master')
          remote {
            credentials('lciadm100_private_key')
            url("${GERRIT_MIRROR}/OSS/ENM-Parent/SQ-Gate/com.ericsson.de/cenm-kgb-integration")
          }
          extensions {
            cleanBeforeCheckout()
          }
        }
      }
  steps {
      dsl {
        text(readFileFromWorkspace('Jenkins/JobDSL/CNRA_Wrapper_Jobs_Rollout.groovy'))
        removeAction('DELETE')
    }
  }
}
job('CNRA_CRD_Job_Rollout'){
  label("${CNRA_Wrapper_Job_Agents}")
  concurrentBuild(allowConcurrentBuild = true)
  logRotator {
    numToKeep(30)
  }
  parameters {
    stringParam('CNRA_CRD_Job_Agents', "${CNRA_CRD_Job_Agents}",'The agents configured to run the CRD jobs')
  }
  scm {
        git {
          branch('master')
          remote {
            credentials('lciadm100_private_key')
            url("${GERRIT_MIRROR}/OSS/ENM-Parent/SQ-Gate/com.ericsson.de/cenm-kgb-integration")
          }
          extensions {
            cleanBeforeCheckout()
          }
        }
      }
  steps {
      dsl {
        text(readFileFromWorkspace('Jenkins/JobDSL/CNRA_CRD_Job_Rollout.groovy'))
    }
  }
}