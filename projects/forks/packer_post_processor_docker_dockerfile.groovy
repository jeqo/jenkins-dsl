freeStyleJob('update_fork_packer_post_processor_docker_dockerfile') {
    displayName('update-fork-packer-post-processor-docker-dockerfile')
    description('Rebase the primary branch (master) in jeqo/packer-post-processor-docker-dockerfile fork.')

    checkoutRetryCount(3)

    properties {
        githubProjectUrl('https://github.com/jeqo/packer-post-processor-docker-dockerfile')
        sidebarLinks {
            link('https://github.com/cornfeedhobo/packer-post-processor-docker-dockerfile', 'UPSTREAM: cornfeedhobo/packer-post-processor-docker-dockerfile', 'notepad.png')
        }
    }

    logRotator {
        numToKeep(2)
        daysToKeep(2)
    }

    scm {
        git {
            remote {
                url('git@github.com:jeqo/packer-post-processor-docker-dockerfile.git')
                name('origin')
                credentials('ssh-github-key')
                refspec('+refs/heads/master:refs/remotes/origin/master')
            }
            remote {
                url('https://github.com/cornfeedhobo/packer-post-processor-docker-dockerfile.git')
                name('upstream')
                refspec('+refs/heads/master:refs/remotes/upstream/master')
            }
            branches('master', 'upstream/master')
            extensions {
                disableRemotePoll()
                wipeOutWorkspace()
                cleanAfterCheckout()
            }
        }
    }

    triggers {
        cron('H H * * *')
    }

    wrappers { colorizeOutput() }

    steps {
        shell('git rebase upstream/master')
    }

    publishers {
        postBuildScripts {
            git {
                branch('origin', 'master')
                pushOnlyIfSuccess()
            }
        }

        extendedEmail {
            recipientList('$DEFAULT_RECIPIENTS')
            contentType('text/plain')
            triggers {
                stillFailing {
                    attachBuildLog(true)
                }
            }
        }

        wsCleanup()
    }
}
