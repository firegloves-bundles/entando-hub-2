import { Link } from 'react-router-dom';
import './not-found.scss';

function NotFound() {
  return (
    <div className="NotFound">
      <h1>404 - Not Found</h1>
      <p>The requested resource could not be found.</p>
      <Link to="/">Go to Home</Link>
    </div>
  );
}

export default NotFound;
