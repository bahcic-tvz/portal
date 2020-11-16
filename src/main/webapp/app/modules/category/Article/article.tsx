import React, { useEffect } from 'react';
import { Col, Row } from 'reactstrap';
import { getArticleById } from "app/entities/article/article.reducer";
import { connect } from 'react-redux';
import { useParams } from 'react-router-dom';
import { convertDateTimeForArticle, getDayCroatian } from "app/shared/util/date-utils";

interface ICategoryProp extends StateProps, DispatchProps {}

const Article = (props: ICategoryProp) => {
  const { article } = useParams()
  const articleId = article.split('-')[article.split('-').length - 1] as number
  // const author = props.article.author
  // const authorName = author.firstName + ' ' + author.lastName

  useEffect(() => {
    props.getArticleById(articleId);
  }, [article])

  return (
    <>
      <Row>
        <Col lg={{ size: 6, offset: 2 }} md={{size: 10, offset: 1}} sm={{size: 12, offset: 0}}>
          <div>
            <div>{props.article.tag}</div>
            <h1>{props.article.title}</h1>
            <span>Piše Nepoznati Autor</span>,{' '}
            <span>{getDayCroatian(props.article.posted)}</span>{', '}
            <span>{convertDateTimeForArticle(props.article.posted)}</span>
          </div>
          <div>
            <img src={props.article.photoURL} alt={`Photo by ${article.photoAuthor}`} width={'100%'}/>
          </div>
          <div>
            <span>Foto: {props.article.photoAuthor}</span>
          </div>
          <div>
            <p>{props.article.summary}</p>
          </div>
          <div>
            <div className={"social-media"}>
              Twitter, Facebook, ... Komentari
            </div>
            <div>
              {props.article.content}
            </div>
            <div>
              Komentari
            </div>
          </div>
        </Col>
        <Col lg={{ size: 2, offset: 0 }} md={{size: 10, offset: 1}} sm={{size: 12, offset: 0}}>
          Side
        </Col>
      </Row>
    </>
  )
};

const mapStateToProps = storeState => ({
  account: storeState.authentication.account,
  isAuthenticated: storeState.authentication.isAuthenticated,
  article: storeState.article.entity,
  loading: storeState.article.loading
});

const mapDispatchToProps = {
  getArticleById,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(Article);
