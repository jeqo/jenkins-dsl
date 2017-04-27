freeStyleJob('update_fork_ansible_role_oracle_weblogic') {
    displayName('update-fork-ansible-role-oracle-weblogic')
    description('Rebase the primary branch (master) in jeqo/ansible-role-oracle-weblogic fork.')

    checkoutRetryCount(3)

    properties {
        githubProjectUrl('https://github.com/jeqo/ansible-role-oracle-weblogic')
        sidebarLinks {
            link('https://github.com/sysco-middleware/ansible-role-oracle-weblogic', 'UPSTREAM: sysco-middleware/ansible-role-oracle-weblogic', 'notepad.png')
        }
    }

    logRotator {
        numToKeep(2)
        daysToKeep(2)
    }

    scm {
        git {
            remote {
                url('git@github.com:jeqo/ansible-role-oracle-weblogic.git')
                name('origin')
                credentials('ssh-github-key')
                refspec('+refs/heads/master:refs/remotes/origin/master')
            }
            remote {
                url('https://github.com/sysco-middleware/ansible-role-oracle-weblogic.git')
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
