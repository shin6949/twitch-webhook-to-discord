package me.cocoblue.twitchwebhook.util;

import notion.api.v1.model.pages.PageProperty;

import java.util.List;

public class NotionPropertyUtil {
    public static String buildStringFromNotionRichText(List<PageProperty.RichText> richTextList) {
        if (richTextList != null && !richTextList.isEmpty()) {
            final StringBuilder plainTextBuilder = new StringBuilder();
            for (final PageProperty.RichText richText : richTextList) {
                if (richText == null || richText.getText() == null) {
                    continue;
                }

                final String textContent = richText.getText().getContent();
                if (textContent != null) {
                    plainTextBuilder.append(textContent);
                }
            }
            return plainTextBuilder.toString();
        } else {
            return null;
        }
    }
}
