freeStyleJob('update_fork_kafka_embedded') {
    displayName('update-fork-kafka-embedded')
    description('Rebase the primary branch (develop) in jeqo/kafka-embedded fork.')

    checkoutRetryCount(3)

    properties {
        githubProjectUrl('https://github.com/jeqo/kafka-embedded')
        sidebarLinks {
            link('https://github.com/miguno/kafka-embedded', 'UPSTREAM: miguno/kafka-embedded', 'notepad.png')
        }
    }

    logRotator {
        numToKeep(2)
        daysToKeep(2)
    }

    scm {
        git {
            remote {
                url('git@github.com:jeqo/kafka-embedded.git')
                name('origin')
                credentials('ssh-github-key')
                refspec('+refs/heads/develop:refs/remotes/origin/develop')
            }
            remote {
                url('https://github.com/miguno/kafka-embedded.git')
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
