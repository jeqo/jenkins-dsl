freeStyleJob('update_fork_terraform_provider_kafka') {
    displayName('update-fork-terraform-provider-kafka')
    description('Rebase the primary branch (develop) in jeqo/terraform-provider-kafka fork.')

    checkoutRetryCount(3)

    properties {
        githubProjectUrl('https://github.com/jeqo/terraform-provider-kafka')
        sidebarLinks {
            link('https://github.com/packetloop/terraform-provider-kafka', 'UPSTREAM: packetloop/terraform-provider-kafka', 'notepad.png')
        }
    }

    logRotator {
        numToKeep(2)
        daysToKeep(2)
    }

    scm {
        git {
            remote {
                url('git@github.com:jeqo/terraform-provider-kafka.git')
                name('origin')
                credentials('ssh-github-key')
                refspec('+refs/heads/develop:refs/remotes/origin/develop')
            }
            remote {
                url('https://github.com/packetloop/terraform-provider-kafka.git')
                name('upstream')
                refspec('+refs/heads/develop:refs/remotes/upstream/develop')
            }
            branches('develop', 'upstream/develop')
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
        shell('git rebase upstream/develop')
    }

    publishers {
        postBuildTask {
            git {
                branch('origin', 'develop')
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
