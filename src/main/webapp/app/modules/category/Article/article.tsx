import './style.scss'
import React, { useEffect, useRef } from 'react';
import { Col, Row } from 'reactstrap';
import { getArticleById } from "app/entities/article/article.reducer";
import { connect } from 'react-redux';
import { useParams } from 'react-router-dom';
import { convertDateTimeForArticle, getDayCroatian } from "app/shared/util/date-utils";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { toast } from 'react-toastify';
import {
  FacebookShareButton,
  TwitterShareButton,
  WhatsappShareButton,
  FacebookMessengerShareButton
} from "react-share";
import Comments from "app/modules/category/Article/Comments/comments";
import {getCommentsForArticle} from "app/entities/comment/comment.reducer";

enum tagColor {
  news = '#D22328',
  show = '#F5A528',
  sport = '#40B14D',
  lifestyle = '#EFC20C',
  tech = '#47C0FF',
  viral = '#297AF6',
  video = '#9757F6',
  sponsored = '#47C0FF'
}

interface ICategoryProp extends StateProps, DispatchProps {}

const Article = (props: ICategoryProp) => {
  const { article, category } = useParams()
  const articleId = article.split('-')[article.split('-').length - 1] as number
  // const author = props.article.author
  // const authorName = author.firstName + ' ' + author.lastName
  const myRef = useRef(null)
  const executeScroll = () => {
    const y = myRef.current.getBoundingClientRect().top + window.pageYOffset - 100;
    window.scrollTo({top: y, behavior: 'smooth'});
  }

  useEffect(() => {
    props.getArticleById(articleId);
    props.getCommentsForArticle(articleId);
  }, [article])

  return (
    <>
      <Row>
        <Col
          lg={{ size: 6, offset: 2 }}
          md={{ size: 10, offset: 1 }}
          sm={{ size: 12, offset: 0 }}
          className={"col-wrapper"}
        >
          <div className={"top-content"}>
            <div
              className={"article-tag"}
              style={{backgroundColor: tagColor[category.toLowerCase()]}}>
              {props.article.tag}
            </div>
            <h1>{props.article.title}</h1>
            <span>Piše{' '}
              <span className={"author-name"}>Nepoznati Autor</span>
            </span>{', '}
            <span>{getDayCroatian(props.article.posted)}</span>{', '}
            <span>{convertDateTimeForArticle(props.article.posted)}</span>
          </div>
          <div>
            <img src={props.article.photoURL} alt={`Photo by ${article.photoAuthor}`} width={'100%'}/>
          </div>
          <div className={"photo-author"}>
            <span>Foto: {props.article.photoAuthor}</span>
          </div>
          <div className={"article-summary"}>
            <p>{props.article.summary}</p>
          </div>
          <div className={"article-content-wrapper"}>
            <div className={"social-media"}>
              <span className={"comments"} onClick={executeScroll} >
                <FontAwesomeIcon icon={["far", "comments"]} />{' '}
                <span>Komentari</span>{' '}
                <span className={"count"}>
                  {props.comments.length}
                </span>
              </span>
              <FacebookShareButton url={window.location.href}>
                <FontAwesomeIcon icon={["fab", "facebook"]} className={"facebook"} />{' '}
              </FacebookShareButton>
              <FacebookMessengerShareButton url={window.location.href} appId={'undefined'}>
                <FontAwesomeIcon icon={["fab", "facebook-messenger"]} className={"facebook-messenger"} />{' '}
              </FacebookMessengerShareButton>
              <TwitterShareButton url={window.location.href}>
                <FontAwesomeIcon icon={["fab", "twitter"]} className={"twitter"} />{' '}
              </TwitterShareButton>
              <WhatsappShareButton url={window.location.href}>
                <FontAwesomeIcon icon={["fab", "whatsapp"]} className={"whatsapp"} />{' '}
              </WhatsappShareButton>
              <FontAwesomeIcon
                icon={["fas", "link"]} className={"link"}
                onClick={() => {
                  navigator.clipboard.writeText(window.location.href)
                  toast.success('Link kopiran u meduspremnik!');
                }}
              />{' '}
            </div>
            <div>
              {props.article.content}
            </div>
            <div ref={myRef}>
              <Comments
                articleId={articleId}
                comments={props.comments}
              />
            </div>
          </div>
        </Col>
        <Col
          lg={{ size: 6, offset: 2 }}
          md={{ size: 10, offset: 1 }}
          sm={{ size: 12, offset: 0 }}
          className={"col-wrapper"}
        >
          Side
        </Col>
      </Row>
    </>
  )
};

const mapStateToProps = storeState => ({
  article: storeState.article.entity,
  comments: storeState.comment.entities
});

const mapDispatchToProps = {
  getArticleById,
  getCommentsForArticle,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(Article);
