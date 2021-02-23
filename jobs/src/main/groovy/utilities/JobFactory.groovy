package utilities

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Folder
import javaposse.jobdsl.dsl.jobs.WorkflowJob

class JobFactory {
  private static final String HOST = 'git.example.com'
  private static final String PIPELINE_ORG = 'Managed-Pipelines'
  private static final String PIPELINE_REPO_NAME = 'Java'
  private static final String PIPELINE_REPO_BRANCH = 'master'
  private static final String PIPELINE_GIT_ORG_URL = "https://$HOST/$PIPELINE_ORG"
  private static final String PIPELINE_GIT_REPO_URL = "$PIPELINE_GIT_ORG_URL/$PIPELINE_REPO_NAME"
  private static final String SCM_CREDENTIALS_ID = '...'
  private static final String BASE_BUILD_PATH = 'Managed-Pipeline/Java'

  static WorkflowJob seedJob(DslFactory factory) {
    factory.pipelineJob("$BASE_BUILD_PATH/_Seed") {
      description 'Pipeline to seed the Managed Pipeline jobs for Java'
      definition {
        cpsScm {
          scm {
            git {
              remote {
                url PIPELINE_GIT_REPO_URL
              }
              branch PIPELINE_REPO_BRANCH
            }
            scriptPath 'jobs/seed.Jenkinsfile'
          }
        }
      }
    }
  }
}
