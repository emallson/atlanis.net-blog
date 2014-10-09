;;; export-post --- Summary
;;; Commentary:
;;; Code:
(progn
  (load "~/.emacs.d/custom.el")
  (package-initialize)
  (require 'cl)
  (add-to-list 'custom-theme-load-path "~/.emacs.d/themes")
  (load-theme 'zenburn t)
  (require 'org)
  (find-file (car (last command-line-args)))
  (setq org-confirm-babel-evaluate nil)
  (if (boundp 'org-export-as-html)
      (org-export-as-html 3 nil "html" t nil)
    (org-html-export-as-html nil nil nil t nil))
  (princ (buffer-string)))
;;; export-post.el ends here
