import { Content } from "carbon-components-react";
import { useState, useEffect } from "react";
import { useParams } from "react-router";
import CatalogPageHeader from "./catalog-page-header/CatalogPageHeader";
import { getSingleBundleGroup } from "../../integration/Integration";

const DetailPage = () => {
  const [bundleGroup, setBundleGroup] = useState({});
  const { id: bundleGroupId } = useParams();

  // fetches the bundle group
  useEffect(() => {
    const init = async () => {
      const fetchedBundleGroup = await getSingleBundleGroup(bundleGroupId);
      setBundleGroup(fetchedBundleGroup.bundleGroup);
    };

    init();
  }, [bundleGroupId]);

  return (
    <>
      <CatalogPageHeader />
      <Content>
        <div className="bx--grid bx--grid--full-width catalog-page">
          <div>
            <div>IMAGE</div>
            <div>Install Button</div>
            <div>Last Update</div>
            <div>Link to Repository</div>
            <div>Link to documentation {bundleGroup.documentationUrl}</div>
            <div>List of Bundles {bundleGroup.children}</div>
          </div>
          <div>
            <div>Product Details</div>
            <div>Version</div>
            <div>Categories {bundleGroup.categories}</div>
            <div>Organization {bundleGroup.organisationId}</div>
            <div>DESCRIPTION {bundleGroup.description}</div>
            <div>IMAGES</div>
          </div>
        </div>
      </Content>
    </>
  );
};

export default DetailPage;
