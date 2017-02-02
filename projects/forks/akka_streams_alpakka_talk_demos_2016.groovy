freeStyleJob('update_fork_akka_streams_alpakka_talk_demos_2016') {
    displayName('update-fork-akka-streams-alpakka-talk-demos-2016')
    description('Rebase the primary branch (master) in jeqo/akka-streams-alpakka-talk-demos-2016 fork.')

    checkoutRetryCount(3)

    properties {
        githubProjectUrl('https://github.com/jeqo/akka-streams-alpakka-talk-demos-2016')
        sidebarLinks {
            link('https://github.com/ktoso/akka-streams-alpakka-talk-demos-2016', 'UPSTREAM: ktoso/akka-streams-alpakka-talk-demos-2016', 'notepad.png')
        }
    }

    logRotator {
        numToKeep(2)
        daysToKeep(2)
    }

    scm {
        git {
            remote {
                url('git@github.com:jeqo/akka-streams-alpakka-talk-demos-2016.git')
                name('origin')
                credentials('ssh-github-key')
                refspec('+refs/heads/master:refs/remotes/origin/master')
            }
            remote {
                url('https://github.com/ktoso/akka-streams-alpakka-talk-demos-2016.git')
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
