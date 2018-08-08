freeStyleJob('update_fork_kafka_site') {
    displayName('update-fork-kafka-site')
    description('Rebase the primary branch (asf-site) in jeqo/kafka-site fork.')

    checkoutRetryCount(3)

    properties {
        githubProjectUrl('https://github.com/jeqo/kafka-site')
        sidebarLinks {
            link('https://github.com/apache/kafka-site', 'UPSTREAM: apache/kafka-site', 'notepad.png')
        }
    }

    logRotator {
        numToKeep(2)
        daysToKeep(2)
    }

    scm {
        git {
            remote {
                url('git@github.com:jeqo/kafka-site.git')
                name('origin')
                credentials('ssh-github-key')
                refspec('+refs/heads/asf-site:refs/remotes/origin/asf-site')
            }
            remote {
                url('https://github.com/apache/kafka-site.git')
                name('upstream')
                refspec('+refs/heads/asf-site:refs/remotes/upstream/asf-site')
            }
            branches('asf-site', 'upstream/asf-site')
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
        shell('git rebase upstream/asf-site')
    }

    publishers {
        postBuildTask {
            git {
                branch('origin', 'asf-site')
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
