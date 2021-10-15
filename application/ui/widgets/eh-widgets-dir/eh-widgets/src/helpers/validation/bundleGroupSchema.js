import * as Yup from "yup"

export const bundleGroupSchema = Yup.object().shape({
  name: Yup.string()
    .min(4, "Name must be at least 4 characters")
    .max(25, "Name must not exceed 25 characters")
    .required("Name is a required field"),
  description: Yup.string()
    .min(4, "Description must be at least 4 characters")
    .max(600, "Description must not exceed 600 characters")
    .required("Description is a required field"),
  documentationUrl: Yup.string()
    .matches(
      /[-a-zA-Z0-9@:%_\+.~#?&//=]{2,256}\.[a-z]{2,4}\b(\/[-a-zA-Z0-9@:%_\+.~#?&//=]*)?/gi,
      "Documentation must match URL format"
    )
    .required("Documentation is a required field"),
  status: Yup.string().required("Status is a required field"),
  version: Yup.string().matches(/^([0-9]|[1-9][0-9]*)\.([0-9]|[1-9][0-9]*)\.([0-9]|[1-9][0-9]*)(?:-([0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*))?(?:\+([0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*))?$/gm, "Version must match semantic versioning format (e.g. x.x.x)").required("Version is a required field"),
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
  categories: Yup.array()
    .of(Yup.string())
    .required("Category is a required field"),
})

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
