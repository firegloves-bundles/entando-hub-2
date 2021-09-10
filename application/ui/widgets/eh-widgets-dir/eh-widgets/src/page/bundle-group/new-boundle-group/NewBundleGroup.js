import { Form } from "carbon-components-react";
import { useEffect, useState } from "react";
import { getAllBundlesForABundleGroup } from "../../../integration/Integration";

const NewBundleGroup = () => {
  const [bundles, setBundles] = useState([]);

  useEffect(() => {
    const init = async () => {
      const bundles = await getAllBundlesForABundleGroup(69);
      setBundles(bundles);
    };

    init();
  }, []);

  console.log(bundles);

  return (
    <>
      <Form>
        <div></div>
      </Form>
    </>
  );
};

export default NewBundleGroup;
