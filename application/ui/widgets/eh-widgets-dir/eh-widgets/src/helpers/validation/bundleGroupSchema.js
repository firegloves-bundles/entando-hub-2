import * as Yup from "yup"
import {BUNDLE_STATUS, BUNDLE_URL_REGEX, CONTACT_URL_REGEX, DOCUMENTATION_ADDRESS_URL_REGEX} from "../constants"

export const bundleGroupSchema = Yup.object().shape({
  name: Yup.string()
    .min(3, "min3Char")
    .max(25, "max25Char")
    .required("nameRequired"),
  description: Yup.string()
    .min(3, "minDescription")
    .max(600, "maxDescription")
    .required("descriptionRequired"),
  documentationUrl: Yup.string()
    .matches(
      DOCUMENTATION_ADDRESS_URL_REGEX,
      "docFormat"
    )
    .max(255, "max255Char")
    .required("docRequired"),
  contactUrl: Yup.string()
    .nullable()
    .matches(
        CONTACT_URL_REGEX,
        "contactUrlFormat"
    )
    .max(255, "max255Char"),
  status: Yup.string().required("statusRequired"),
  version: Yup.string().matches(/^[v]?([0-9]|[1-9][0-9]*)\.([0-9]|[1-9][0-9]*)\.([0-9]|[1-9][0-9]*)(?:-([0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*))?(?:\+([0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*))?$/gm, "versionFormat").required("versionRequired"),
  children: Yup.array().of(
    Yup.object().shape({
      bundleGroups: Yup.array().of(Yup.string()),
      // bundleId: Yup.number().positive().integer(),
      dependencies: Yup.array().of(Yup.string()),
      description: Yup.string(),
      gitRepoAddress: Yup.string(),
      name: Yup.string().required(),
    })
  ).min(1, "atleastOneUrl"),
  categories: Yup.array()
    .of(Yup.string())
    .required("categoryRequired"),
})


// New bundleGroupSchema
export const newBundleGroupSchema = Yup.object().shape({
  name: Yup.string()
      .min(3, "min3Char")
      .max(25, "max25Char")
      .required("nameRequired"),
  versionDetails: Yup.object().shape({
      description: Yup.string().min(3, "minDescription").max(600, "maxDescription").required("descriptionRequired"),
      contactUrl: Yup.string()
          .nullable()
          .when('displayContactUrl', {
              is: (displayContactUrl) => (displayContactUrl === true),
              then: Yup.string()
                  .required('contactUrlRequired')
                  .matches(
                      CONTACT_URL_REGEX,
                      "contactUrlFormat"
                  )
          })
          .max(255, "max255Char"),
      displayContactUrl: Yup.boolean()
          .nullable(),
      documentationUrl: Yup.string()
          .matches(
              DOCUMENTATION_ADDRESS_URL_REGEX,
              "docFormat"
          )
          .max(255, "max255Char")
          .required("docRequired"),
      version: Yup.string().matches(/^[v]?([0-9]|[1-9][0-9]*)\.([0-9]|[1-9][0-9]*)\.([0-9]|[1-9][0-9]*)(?:-([0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*))?(?:\+([0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*))?$/gm, "versionFormat").required("versionRequired"),
      status: Yup.string().required("statusRequired"),
      bundles: Yup.array().of(
          Yup.object().shape({
              bundleGroups: Yup.array().of(Yup.string()),
              // bundleId: Yup.number().positive().integer(),
              dependencies: Yup.array().of(Yup.string()),
              description: Yup.string(),
              gitRepoAddress: Yup.string(),
              name: Yup.string().required(),
          })
      ).when(['displayContactUrl','status'], {
              is: (displayContactUrl, status) => {
                  return (displayContactUrl !== true) && ((status === BUNDLE_STATUS.PUBLISH_REQ) || (status === BUNDLE_STATUS.PUBLISHED))
              },
              then: Yup.array().min(1, "atleastOneUrl")},
      )
  }, [['status','displayContactUrl', 'contactUrl', 'bundles']]
  ).required(),
  categories: Yup.array()
      .of(Yup.string())
      .required("categoryRequired"),
})

export const bundleOfBundleGroupSchema = Yup.object().shape({
  gitRepo: Yup.string()
      .required("bundleUrlRequired")
      .matches(
        BUNDLE_URL_REGEX,
          "bundleUrlFormat"
      )
      .max(255, "max255Char")
})
