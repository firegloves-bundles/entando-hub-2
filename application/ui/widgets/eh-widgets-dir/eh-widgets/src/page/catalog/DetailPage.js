import { Content } from "carbon-components-react";
import { useState, useEffect } from "react";
import { useParams } from "react-router";
import CatalogPageHeader from "./catalog-page-header/CatalogPageHeader";
import { getSingleBundleGroup } from "../../integration/Integration";

const DetailPage = () => {
  const [bundleGroup, setBundleGroup] = useState({});
  const { id: bundleGroupId } = useParams();

  useEffect(() => {
    const init = async () => {
      const fetchedBundleGroup = await getSingleBundleGroup(bundleGroupId);
      setBundleGroup(fetchedBundleGroup.bundleGroup);
    };

    init();
  }, [getSingleBundleGroup]);

  console.log(bundleGroup);

  return (
    <>
      <CatalogPageHeader />
      <Content>Detail page</Content>
    </>
  );
};

export default DetailPage;
