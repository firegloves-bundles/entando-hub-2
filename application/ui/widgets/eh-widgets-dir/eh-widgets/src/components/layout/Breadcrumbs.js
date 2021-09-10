import React from "react";
import { Link, useHistory } from "react-router-dom";

const Breadcrumbs = () => {
  const history = useHistory();
  const pathname = history.location.pathname;
  // splits the pathname into an array and puts a space in front of every capital letter
  const breadcrumbs = pathname
    .split("/")
    .filter((item) => {
      return item !== "";
    })
    .map((item) => {
      return item.replace(/([A-Z])/g, " $1").trim();
    });

  // creates an array with all the paths for all the breadcrumbs
  let breadcrumbsPaths = [];
  for (let i = 0; i < breadcrumbs.length; i++) {
    let tmp = "/";
    for (let j = 0; j <= i; j++) {
      tmp += breadcrumbs[j];
      if (j !== i) {
        tmp += "/";
      }
    }
    breadcrumbsPaths.push(tmp);
  }

  // creates breadcrumb links to be rendered
  let breadcrumbsLinks = [];
  for (let i = 0; i < breadcrumbs.length; i++) {
    let tmp;
    if (i !== breadcrumbs.length - 1) {
      tmp = (
        <li>
          <Link to={breadcrumbsPaths[i]} key={breadcrumbsPaths[i]}>
            {breadcrumbs[i].toLowerCase()}
          </Link>
        </li>
      );
    } else {
      tmp = <li>{breadcrumbs[i].toLowerCase()}</li>;
    }
    breadcrumbsLinks.push(tmp);
  }

  return <div>{breadcrumbsLinks}</div>;
};

export default Breadcrumbs;
