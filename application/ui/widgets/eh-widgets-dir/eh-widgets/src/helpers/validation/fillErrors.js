import i18n from "../../i18n"

export const fillErrors = (yupError) => {
    return yupError.inner
      .map((entry) => {
        return { path: entry.path, message: entry.message }
      })
      .reduce((previousValue, currentValue) => {
        const ret = { ...previousValue }
        const previousPathMessages = previousValue[currentValue.path]
        if (previousPathMessages) {
          ret[currentValue.path] = previousPathMessages.concat([
            currentValue.message === "Must be a git repo URL (e.g. https://github.com/reponame/myrepo.git or git@github.com:github/reponame/myrepo.git)" ? i18n.t('formValidationMsg.bundleUrlFormat') : 
            i18n.t(currentValue.message),
          ])
        } else {
          ret[currentValue.path] = [i18n.t(currentValue.message)]
        }
        
        let errMap = {
          version: {
            versionPattern: '[missing "it.Version must match semantic versioning format (e.g. vx.x.x or x.x.x)" translation]'
          },
        };
        if (ret.version) {
          for (let index = 0; index < ret.version.length; index++) {
            if (ret.version[index] === errMap.version.versionPattern) {
              ret.version[index] = i18n.t('formValidationMsg.versionFormat')
            }
          }
        }
        return ret

      }, {})
  }