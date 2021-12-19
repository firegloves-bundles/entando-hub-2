import * as Yup from "yup"

export const organisationSchema = Yup.object().shape({
  name: Yup.string()
    .min(3, "min3Char")
    .max(25, "max25Char")
    .required("nameRequired"),
  description: Yup.string()
    .max(100, "description"),
})
