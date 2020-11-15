import './articlePreview.scss';
import React from 'react';
import { IArticle } from "app/shared/model/article.model";
import { Col } from 'reactstrap';
import { Link } from 'react-router-dom';

interface Props {
  index: number
  article: IArticle
}

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

const generateArticle = (index: number) => {
  const heroHeight = 500
  let lg, md, sm, color, height
  switch (index) {
    case 0:
      lg = {size: 9, offset: 0}
      md = {size: 12, offset: 0}
      sm = {size: 12, offset: 0}
      color = 'red'
      height = heroHeight
      break
    case 1:
      lg = {size: 3, offset: 0}
      md = {size: 12, offset: 0}
      sm = {size: 12, offset: 0}
      color = 'blue'
      height = heroHeight
      break
    default:
      lg = {size: 4, offset: 0}
      md = {size: 6, offset: 0}
      sm = {size: 12, offset: 0}
      color = 'green'
      height = heroHeight / 2
      break
  }

  return {lg, md, sm, color, height}
}

export const ArticlePreview = ({ index, article }: Props) => {
  const { lg, md, sm, color, height } = generateArticle(index)
  const dashedTitle = article.title.toLowerCase().replace(/[\W_]+/g,"-")
  const linkTo = `/category/news/${dashedTitle}-${article.id}`
  return (
    <Col lg={lg} md={md} sm={sm} style={{backgroundColor: 'transparent'}}>
      <Link to={linkTo} className="preview-link">
        <div className="article-preview-container">
          <div className={"image-container"}>
            <img src={article.photoURL} alt={`Photo by ${article.photoAuthor}`} width={'100%'}/>
            <div
              className={"bottom-left-tag"}
              style={{backgroundColor: tagColor[article.category.toLowerCase()]}}
            >
              {article.tag}
            </div>
          </div>
          {article.hero && (
            <h1>{article.title}</h1>
          )}
          {!article.hero && (
            <div>
              <h2>{article.title}</h2>
              <p>{article.summary}</p>
            </div>
          )}
        </div>
      </Link>
    </Col>
  )
}

export default ArticlePreview
