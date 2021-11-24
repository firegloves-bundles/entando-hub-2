import * as Yup from "yup"

export const bundleGroupSchema = Yup.object().shape({
  name: Yup.string()
    .min(3, "Name must be at least 3 characters")
    .max(25, "Name must not exceed 25 characters")
    .required("Name is a required field"),
  description: Yup.string()
    .min(3, "Description must be at least 3 characters")
    .max(600, "Description must not exceed 600 characters")
    .required("Description is a required field"),
  documentationUrl: Yup.string()
    .matches(
      /[-a-zA-Z0-9@:%_+.~#?&//=]{2,256}\.[a-z]{2,4}\b(\/[-a-zA-Z0-9@:%_+.~#?&//=]*)?/gi,
      "Documentation must match URL format"
    )
    .required("Documentation is a required field"),
  status: Yup.string().required("Status is a required field"),
  version: Yup.string().matches(/^[v]?([0-9]|[1-9][0-9]*)\.([0-9]|[1-9][0-9]*)\.([0-9]|[1-9][0-9]*)(?:-([0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*))?(?:\+([0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*))?$/gm, "Version must match semantic versioning format (e.g. vx.x.x or x.x.x)").required("Version is a required field"),
  children: Yup.array().of(
    Yup.object().shape({
      bundleGroups: Yup.array().of(Yup.string()),
      // bundleId: Yup.number().positive().integer(),
      dependencies: Yup.array().of(Yup.string()),
      description: Yup.string(),
      gitRepoAddress: Yup.string(),
      name: Yup.string().required(),
    })
  ).min(1, "Please add at least one bundle before publishing this bundle group."),
  categories: Yup.array()
    .of(Yup.string())
    .required("Category is a required field"),
})


export const bundleOfBundleGroupSchema = Yup.object().shape({
    gitRepo: Yup.string()
        .required("The bundle address is a required field")
        .matches(
            /[-a-zA-Z0-9@:%_+.~#?&//=]{2,256}\.[a-z]{2,4}\b(\/[-a-zA-Z0-9@:%_+.~#?&//=]*)?.git/gi,
            "Must be a git repo URL (e.g. https://github.com/myrepo.git)"
        )
})




