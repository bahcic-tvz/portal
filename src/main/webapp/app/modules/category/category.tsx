import './category.scss';
import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Row, Col } from 'reactstrap';
import { useParams } from 'react-router-dom';
import { getArticlesByCategory } from "app/entities/article/article.reducer";
import {IArticle} from "app/shared/model/article.model";
import ArticlePreview from "app/modules/category/ArticlePreview/articlePreview";

interface ICategoryProp extends StateProps, DispatchProps {}

export const Category = (props: ICategoryProp) => {
  const { account } = props;
  const { category } = useParams();

  useEffect(() => {
    props.getArticlesByCategory(category); // TODO: get sorted, hero first, then by date ... also w\out content
  }, [category])

  return (
    <>
      <Row>
        <Col lg={{size: 8, offset: 2}} md={{size: 10, offset: 1}} sm={{size: 12, offset: 0}}>
          <Row>
            {props.articleList.map((article: IArticle, index: number) => (
              <ArticlePreview
                key={index}
                index={index}
                article={article}
              />
            ))}
          </Row>
        </Col>
      </Row>
    </>
  );
};

const mapStateToProps = storeState => ({
  account: storeState.authentication.account,
  isAuthenticated: storeState.authentication.isAuthenticated,
  articleList: storeState.article.entities,
  loading: storeState.article.loading,
});

const mapDispatchToProps = {
  getArticlesByCategory,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(Category);
