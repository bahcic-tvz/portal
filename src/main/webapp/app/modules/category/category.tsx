import './category.scss';
import React from 'react';
import { connect } from 'react-redux';
import { Row, Col } from 'reactstrap';
import { useParams } from 'react-router-dom';

export type ICategoryProp = StateProps;

export const Category = (props: ICategoryProp) => {
  const { account } = props;
  const { category } = useParams();

  return (
    <Row>
      <Col lg={{size: 8, offset: 2}} md={{size: 10, offset: 1}} sm={{size: 12, offset: 0}}>
        <h2>Category page</h2>
        <p>{category}</p>
      </Col>
    </Row>
  );
};

const mapStateToProps = storeState => ({
  account: storeState.authentication.account,
  isAuthenticated: storeState.authentication.isAuthenticated,
});

type StateProps = ReturnType<typeof mapStateToProps>;

export default connect(mapStateToProps)(Category);
