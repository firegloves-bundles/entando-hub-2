import axios from "axios";
import { useFormik } from "formik";
import React from "react";
import * as Yup from "yup";

const NewOrganisation = () => {
  const urlPostOrganisation = `http://hubdev.okd-entando.org/entando-hub-api/api/organisation/`;

  const formik = useFormik({
    initialValues: { orgName: "", orgDescription: "" },
    validationSchema: Yup.object({
        orgName: Yup.string()
            .max(20, "Must be 20 characters or less")
            .required("Required"),
        orgDescription: Yup.string()
            .max(50, "Must be 50 characters or less")
            .required("Required")
    }),
    onSubmit: async values => {
        await axios.post(urlPostOrganisation, {
            name: values.orgName,
            description: values.orgDescription,
            bundleGroups: []
        }).then(res => {
            console.log(res);
        }).catch(e => {
            console.log(e.toJSON());
        })
    }
  });

  return (
    <form onSubmit={formik.handleSubmit}>
      <label htmlFor="orgName">Name</label>
      <input
        id="orgName"
        type="text"
        {...formik.getFieldProps("orgName")}
      />
      {formik.touched.orgName && formik.errors.orgName ? <div>{formik.errors.orgName}</div> : null}
      <label htmlFor="orgDescription">Description</label>
      <input
        id="orgDescription"
        type="text"
        {...formik.getFieldProps("orgDescription")}
      />
      {formik.touched.orgDescription && formik.errors.orgDescription ? <div>{formik.errors.orgDescription}</div> : null}
      <button type="submit">Submit</button>
    </form>
  );
};

export default NewOrganisation;
