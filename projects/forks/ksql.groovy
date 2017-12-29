freeStyleJob('update_fork_ksql') {
    displayName('update-fork-ksql')
    description('Rebase the primary branch (0.1.x) in jeqo/ksql fork.')

    checkoutRetryCount(3)

    properties {
        githubProjectUrl('https://github.com/jeqo/ksql')
        sidebarLinks {
            link('https://github.com/confluentinc/ksql', 'UPSTREAM: confluentinc/ksql', 'notepad.png')
        }
    }

    logRotator {
        numToKeep(2)
        daysToKeep(2)
    }

    scm {
        git {
            remote {
                url('git@github.com:jeqo/ksql.git')
                name('origin')
                credentials('ssh-github-key')
                refspec('+refs/heads/0.1.x:refs/remotes/origin/0.1.x')
            }
            remote {
                url('https://github.com/confluentinc/ksql.git')
                name('upstream')
                refspec('+refs/heads/0.1.x:refs/remotes/upstream/0.1.x')
            }
            branches('0.1.x', 'upstream/0.1.x')
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
        shell('git rebase upstream/0.1.x')
    }

    publishers {
        postBuildTask {
            git {
                branch('origin', '0.1.x')
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
