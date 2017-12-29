freeStyleJob('update_fork_opentracing_java_elasticsearch_client') {
    displayName('update-fork-opentracing-java-elasticsearch-client')
    description('Rebase the primary branch (master) in jeqo/opentracing-java-elasticsearch-client fork.')

    checkoutRetryCount(3)

    properties {
        githubProjectUrl('https://github.com/jeqo/opentracing-java-elasticsearch-client')
        sidebarLinks {
            link('https://github.com/opentracing-contrib/opentracing-java-elasticsearch-client', 'UPSTREAM: opentracing-contrib/opentracing-java-elasticsearch-client', 'notepad.png')
        }
    }

    logRotator {
        numToKeep(2)
        daysToKeep(2)
    }

    scm {
        git {
            remote {
                url('git@github.com:jeqo/opentracing-java-elasticsearch-client.git')
                name('origin')
                credentials('ssh-github-key')
                refspec('+refs/heads/master:refs/remotes/origin/master')
            }
            remote {
                url('https://github.com/opentracing-contrib/opentracing-java-elasticsearch-client.git')
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
        postBuildTask {
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