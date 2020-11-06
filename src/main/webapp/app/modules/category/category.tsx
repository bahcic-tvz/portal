import './category.scss';
import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Row, Col } from 'reactstrap';
import { useParams } from 'react-router-dom';
import { getArticlesByCategory } from "app/entities/article/article.reducer";

interface ICategoryProp extends StateProps, DispatchProps {}

export const Category = (props: ICategoryProp) => {
  const { account } = props;
  const { category } = useParams();

  useEffect(() => {
    props.getArticlesByCategory(category);
  }, [category])

  return (
    <Row>
      <Col lg={{size: 8, offset: 2}} md={{size: 10, offset: 1}} sm={{size: 12, offset: 0}}>
        {props.articleList.map(article => (
          <div>
            <div>{article.title}</div>
            <div>{article.summary}</div>
            <div>{article.content}</div>
            <br/>
          </div>
        ))}
      </Col>
    </Row>
  );
};

const mapStateToProps = storeState => ({
  account: storeState.authentication.account,
  isAuthenticated: storeState.authentication.isAuthenticated,
  articleList: storeState.article.entities,
  // loading: storeState.storeStatearticle.loading,
});

const mapDispatchToProps = {
  getArticlesByCategory,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(Category);
