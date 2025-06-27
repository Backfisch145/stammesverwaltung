package com.vcp.hessen.kurhessen.core.components;

import com.vaadin.flow.component.upload.UploadI18N;
import com.vcp.hessen.kurhessen.core.i18n.TranslatableText;

import java.util.Arrays;

/**
 * Provides a default I18N configuration for the Upload examples
 * <p>
 * At the moment the Upload component requires a fully configured I18N
 * instance, even for use-cases where you only want to change individual texts.
 * <p>
 * This I18N configuration is an adaption of the web components I18N defaults
 * and can be used as a basis for customizing individual texts.
 */
public class MyUploadI18N extends UploadI18N {
    public MyUploadI18N() {
        setDropFiles(new DropFiles()
                .setOne(new TranslatableText("DropFileHere").translate())
                .setMany(new TranslatableText("DropFilesHere").translate()));
        setAddFiles(new AddFiles()
                .setOne(new TranslatableText("UploadFile").translate())
                .setMany(new TranslatableText("UploadFiles").translate()));
        setError(new Error()
                .setTooManyFiles(new TranslatableText("TooManyFiles").translate())
                .setFileIsTooBig(new TranslatableText("FileTooBig").translate())
                .setIncorrectFileType(new TranslatableText("WrongFileFormat").translate()));
        setUploading(new Uploading()
                .setStatus(new Uploading.Status()
                        .setConnecting("Connecting...")
                        .setStalled("Stalled")
                        .setProcessing("Processing File...")
                        .setHeld("Queued"))
                .setRemainingTime(new Uploading.RemainingTime()
                        .setPrefix("remaining time: ")
                        .setUnknown("unknown remaining time"))
                .setError(new Uploading.Error()
                        .setServerUnavailable("Upload failed, please try again later")
                        .setUnexpectedServerError("Upload failed due to server error")
                        .setForbidden("Upload forbidden")));
        setUnits(new Units()
                .setSize(Arrays.asList("B", "kB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")));
    }
}
