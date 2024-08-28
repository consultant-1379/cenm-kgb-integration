listView('CNRA_Design_Teams') {
     filterBuildQueue()
     filterExecutors()
     jobs {
          regex(/(.*[aAbBcC][0-9]{1,3}|.*_ieatenm.*|.*FLEX.*)$/)
     }
     columns {
          status()
          weather()
          name()
          lastSuccess()
          lastFailure()
          lastDuration()
          buildButton()
     }
}
listView('CNRA_Pipeline_Jobs') {
     filterBuildQueue()
     filterExecutors()
     jobs {
           name('cENM_Add_Licenses')
           name('cENM_Mediation_Pods_Restart')
           name('cENM_Update_Trust_Profile')
           name('cENM_Design_Teams_Install')
           name('cENM_Design_Teams_Uninstall')
           name('cENM_Design_Teams_Upgrade')
           name('cENM_Design_Teams_TAF')
     }
     columns {
          status()
          weather()
          name()
          lastSuccess()
          lastFailure()
          lastDuration()
          buildButton()
     }
}
listView('CNRA_Rollout_Jobs') {
     filterBuildQueue()
     filterExecutors()
     jobs {
          regex(/.*_Rollout$/)
          name('CNRA_Pipeline_Generator')
     }
     columns {
          status()
          weather()
          name()
          lastSuccess()
          lastFailure()
          lastDuration()
          buildButton()
     }
}
