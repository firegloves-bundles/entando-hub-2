import * as Yup from "yup"

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
            currentValue.message,
          ])
        } else {
          ret[currentValue.path] = [currentValue.message]
        }
        return ret
      }, {})
  }