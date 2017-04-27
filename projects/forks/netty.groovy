freeStyleJob('update_fork_netty') {
    displayName('update-fork-netty')
    description('Rebase the primary branch (4.1) in jeqo/netty fork.')

    checkoutRetryCount(3)

    properties {
        githubProjectUrl('https://github.com/jeqo/netty')
        sidebarLinks {
            link('https://github.com/netty/netty', 'UPSTREAM: netty/netty', 'notepad.png')
        }
    }

    logRotator {
        numToKeep(2)
        daysToKeep(2)
    }

    scm {
        git {
            remote {
                url('git@github.com:jeqo/netty.git')
                name('origin')
                credentials('ssh-github-key')
                refspec('+refs/heads/4.1:refs/remotes/origin/4.1')
            }
            remote {
                url('https://github.com/netty/netty.git')
                name('upstream')
                refspec('+refs/heads/4.1:refs/remotes/upstream/4.1')
            }
            branches('4.1', 'upstream/4.1')
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
        shell('git rebase upstream/4.1')
    }

    publishers {
        postBuildTask {
            git {
                branch('origin', '4.1')
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
