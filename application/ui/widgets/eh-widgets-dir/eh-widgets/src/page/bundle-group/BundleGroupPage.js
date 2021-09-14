import {Content, Tile, Grid, Row, Column} from "carbon-components-react";
import {useEffect, useState} from "react";
import {useParams} from "react-router";
import CatalogPageHeaderInternal
  from "../catalog/catalog-page-header-internal/CatalogPageHeaderInternal";
import CatalogPageFooter
  from "../catalog/catalog-page-footer/CatalogPageFooter";

import {
  getAllBundlesForABundleGroup,
  getSingleBundleGroup,
  getSingleCategory,
  getSingleOrganisation
} from "../../integration/Integration";
import EhBreadcrumb from "../../components/eh-bradcrumb/EhBreadcrumb";
import {ModalInstallInformation} from "./modal-install-information/ModalInstallInformation";

import "./bundle-group-page.scss"

/*
BUNDLEGROUP:
{
name	string
description	string
descriptionImage	string
documentationUrl	string
status	string
Enum:
Array [ 2 ]
children	[...]
organisationId	string
categories	[...]
bundleGroupId	string
}


BUNDLE
{
name	string
description	string
gitRepoAddress	string
dependencies	[...]
bundleGroups	[...]
bundleId	string
}
 */

const BundleGroupPage = () => {
  const [bundleGroup, setBundleGroup] = useState({});
  const [organisation, setOrganisation] = useState(null);
  const [category, setCategory] = useState(null);
  const [children, setChildren] = useState([]);
  const {id: bundleGroupId} = useParams();

  // fetches the bundle group
  useEffect(() => {
    const init = async () => {
      const fetchedBundleGroup = (await getSingleBundleGroup(
          bundleGroupId)).bundleGroup;
      setOrganisation(
          fetchedBundleGroup.organisationId ? (await getSingleOrganisation(
              fetchedBundleGroup.organisationId)).organisation : null)
      setCategory(
          fetchedBundleGroup.categories && fetchedBundleGroup.categories.length
          > 0 ? (await getSingleCategory(
              fetchedBundleGroup.categories[0])).category : null)
      setChildren(
          fetchedBundleGroup.children && fetchedBundleGroup.children.length > 0
              ? (await getAllBundlesForABundleGroup(bundleGroupId)).bundleList
              : [])
      setBundleGroup(fetchedBundleGroup);
    };

    init();
  }, [bundleGroupId]);

  return (
      <>
        <CatalogPageHeaderInternal/>
        <Content className="BundleGroupPage">
          <Row className="bx--grid bx--grid--full-width BundleGroupPage-page">
            <div className="bx--row">
              <div className="bx--col-lg-16 BundleGroupPage-breadcrumb">
                <EhBreadcrumb pathElements={[{
                  path: bundleGroup.name,
                  href: window.location.href
                }]}/>
              </div>
            </div>
          </Row>
          <Grid condensed>
            <Row>
              <Column lg={4}>
                <Tile>
                  <div className="BundleGroupPage-image">
                    <img src="/../../Logo-blue.png" alt="Entando logo"/>

                    {bundleGroup && bundleGroup.bundleGroupdescriptionImage}
                  </div>
                  <ModalInstallInformation bundleGroup={bundleGroup}
                                           children={children}/>
                  <div className="BundleGroupPage-last-update">
                    Last Update
                    <p>09/01/2017, 09:00 </p>
                  </div>
                  <hr/>
                  <div className="BundleGroupPage-docs">
                    Link to documentation <br/>
                    {bundleGroup
                    && bundleGroup.documentationUrl}
                    <a href="https://github.com/GermanoGiudici/sample-composition-layer-umd-react-bundle.git"
                       target="_new">test link fake</a>
                  </div>
                  <hr/>
                  <div>
                    {children && <BundleList children={children}/>}
                  </div>
                </Tile>
              </Column>
              <Column lg={12}>
                <Tile>
                  <p className="BundleGroupPage-title">
                    {bundleGroup && bundleGroup.name}
                  </p>

                  <div className="BundleGroupPage-flex">
                    <Column className="BundleGroupPage-specs">
                      Version
                      <p>1.2.0</p>

                    </Column>
                    <Column className="BundleGroupPage-specs">
                      Category
                      <p>{category && category.name}</p>

                    </Column>
                    <Column className="BundleGroupPage-specs">
                      Organization
                      <p>{organisation && organisation.name}</p>

                    </Column>

                  </div>
                  <div className="BundleGroupPage-description">
                    {bundleGroup && bundleGroup.description}
                    Lorem ipsum dolor sit amet, consectetur adipiscing elit.
                    Aenean non posuere elit, at venenatis lectus. Nunc euismod
                    eu nunc malesuada porta. Nulla vulputate tristique nulla,
                    non venenatis velit ullamcorper at. Quisque eleifend tortor
                    et mauris egestas, id sodales metus malesuada. Pellentesque
                    ut justo eleifend erat vulputate accumsan eu eu lorem.
                    Praesent porttitor, quam sed tempor dignissim, odio urna
                    consequat felis, a ullamcorper odio turpis sed dolor. Nullam
                    eleifend facilisis risus vel ultricies. In dapibus nunc at
                    dolor convallis, eget ornare arcu sagittis. Proin ornare
                    dignissim dui ut auctor. Sed at egestas diam, ultrices
                    aliquam ipsum. In rutrum est id sem tristique imperdiet.
                    Aenean finibus quis risus et feugiat. Praesent vehicula
                    turpis in mauris auctor gravida. Pellentesque ultrices
                    lectus et neque consequat semper. Fusce non aliquam felis.
                    Mauris nunc nisi, elementum sed elit eget, mollis aliquet
                    turpis
                  </div>
                  <ul className="BundleGroupPage-image-list">
                    <li>
                      <img src="/../../Tickets 2.png" alt="Entando logo"/>
                    </li>
                    <li>
                      <img src="/../../Tickets1.png" alt="Entando logo"/>
                    </li>
                  </ul>
                </Tile>
              </Column>
            </Row>
          </Grid>
        </Content>
        <CatalogPageFooter/>
      </>
  )
      ;
};

/*
const parseGitRepoAddr = (gitRepoAddress) =>
  {
    return gitRepoAddress ? {
      name: gitRepoAddress.substring(gitRepoAddress.lastIndexOf("/") + 1,
          gitRepoAddress.lastIndexOf(".")),
      gitRepoAddress
    } : {
      name: "",
      gitRepoAddress: ""
    }
  }
*/

const BundleList = (
    {
      children
    }
) => {
  const elemList = children.map((bundle, index) =>
      <li key={index.toString()}><a href={bundle.gitRepoAddress}
                                    target={"_new"}>{bundle.name}</a></li>)

  return (
      <div className="BundleGroupPage-list-wrapper">
        <div className="BundleGroupPage-list">
          List of Bundles
        </div>
        <ul>{elemList}</ul>
      </div>
  )

}

export default BundleGroupPage;
