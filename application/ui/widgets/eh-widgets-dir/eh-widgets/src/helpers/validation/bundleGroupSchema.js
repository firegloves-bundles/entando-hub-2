import * as Yup from "yup"

export const bundleGroupSchema = Yup.object().shape({
  name: Yup.string().min(4).max(25).required(),
  description: Yup.string().min(4).max(600).required(),
  documentationUrl: Yup.string()
    .matches(
      /[-a-zA-Z0-9@:%_\+.~#?&//=]{2,256}\.[a-z]{2,4}\b(\/[-a-zA-Z0-9@:%_\+.~#?&//=]*)?/gi
    )
    .required(),
  status: Yup.string().required(),
  children: Yup.array().of(
    Yup.object().shape({
      bundleGroups: Yup.array().of(Yup.string()),
      // bundleId: Yup.number().positive().integer(),
      dependencies: Yup.array().of(Yup.string()),
      description: Yup.string(),
      gitRepoAddress: Yup.string(),
      name: Yup.string().required(),
    })
  ),
  categories: Yup.array().of(Yup.string()),
})

export const fillErrors = (yupError) => {
    return yupError.inner.map(entry => {
        return {path:entry.path,message:entry.message}
    }).reduce((previousValue, currentValue) => {
        const ret = {...previousValue}
        const previousPathMessages = previousValue[currentValue.path];
        if (previousPathMessages) {
            ret[currentValue.path] = previousPathMessages.concat([currentValue.message])
        }else{
            ret[currentValue.path]=[currentValue.message]

        }
        return ret
    }, {})

}
