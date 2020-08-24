package com.hackernews.api.model.ui;

import com.hackernews.api.model.client.CommentDetails;
import com.hackernews.api.model.client.UserDetails;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CommentDetailsUi {
  private String authorId;
  private int authorActiveTime;
  private String commentText;

  /**
   * Transformation Function for Ui Model.
   *
   * @param commentDetails {@link CommentDetails}
   * @return Corresponding details to be shown on UI.
   */
  public static CommentDetailsUi from(CommentDetails commentDetails,
                                      UserDetails userDetails) {
    LocalDate timeCreated = Instant.ofEpochSecond(userDetails.getCreated())
        .atZone(ZoneId.systemDefault()).toLocalDate();
    return CommentDetailsUi.builder()
        .authorId(commentDetails.getBy())
        .authorActiveTime(Period.between(timeCreated, LocalDate.now()).getYears())
        .commentText(commentDetails.getText())
        .build();
  }
}