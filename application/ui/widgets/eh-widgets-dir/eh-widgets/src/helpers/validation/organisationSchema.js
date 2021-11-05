import * as Yup from "yup"

export const organisationSchema = Yup.object().shape({
  name: Yup.string()
    .min(4, "Name must be at least 4 characters")
    .max(25, "Name must not exceed 25 characters")
    .required("Name is a required field"),
  description: Yup.string()
    .max(100, "Description must not exceed 100 characters"),
})
