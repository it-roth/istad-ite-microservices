import { Button } from '@/components/ui/button'
import { ChevronsLeft, ChevronLeft, ChevronRight, ChevronsRight } from 'lucide-react'
import { useApp } from '@/context/AppContext'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'

export function TablePagination({
  totalItems = 0,
  page = 1,
  pageSize = 5,
  pageSizeOptions = [5, 10, 20, 50],
  onPageChange,
  onPageSizeChange,
  className = '',
}) {
  const { tr } = useApp()
  const totalPages = Math.max(1, Math.ceil(totalItems / pageSize))
  const startIndex = (page - 1) * pageSize
  const endIndex = Math.min(startIndex + pageSize, totalItems)

  return (
    <div className={`flex flex-col sm:flex-row items-center justify-between border-t border-border px-4 py-3 text-xs text-muted-foreground gap-3 bg-card ${className}`}>
      <div>
        {totalItems > 0
          ? `${startIndex + 1}-${endIndex} ${tr('common.of')} ${totalItems}`
          : `0 ${tr('common.of')} 0`}
      </div>

      <div className="flex items-center gap-4 flex-wrap justify-end">
        {onPageSizeChange && (
          <div className="flex items-center gap-2">
            <span>{tr('common.rowsPerPage')}</span>
            <Select
              value={String(pageSize)}
              onValueChange={val => {
                onPageSizeChange(Number(val))
                onPageChange(1)
              }}
            >
              <SelectTrigger className="h-7 w-[68px] text-xs px-2">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {pageSizeOptions.map(opt => (
                  <SelectItem key={opt} value={String(opt)}>
                    {opt}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        )}

        <div className="flex items-center gap-2">
          <span>
            {tr('common.pageOf')} {page} {tr('common.of')} {totalPages}
          </span>
          <div className="flex items-center gap-1">
            <Button
              variant="ghost"
              size="icon"
              className="h-7 w-7 cursor-pointer"
              disabled={page <= 1}
              onClick={() => onPageChange(1)}
              aria-label="First page"
            >
              <ChevronsLeft size={14} />
            </Button>
            <Button
              variant="ghost"
              size="icon"
              className="h-7 w-7 cursor-pointer"
              disabled={page <= 1}
              onClick={() => onPageChange(Math.max(1, page - 1))}
              aria-label="Previous page"
            >
              <ChevronLeft size={14} />
            </Button>
            <Button
              variant="ghost"
              size="icon"
              className="h-7 w-7 cursor-pointer"
              disabled={page >= totalPages}
              onClick={() => onPageChange(Math.min(totalPages, page + 1))}
              aria-label="Next page"
            >
              <ChevronRight size={14} />
            </Button>
            <Button
              variant="ghost"
              size="icon"
              className="h-7 w-7 cursor-pointer"
              disabled={page >= totalPages}
              onClick={() => onPageChange(totalPages)}
              aria-label="Last page"
            >
              <ChevronsRight size={14} />
            </Button>
          </div>
        </div>
      </div>
    </div>
  )
}

export default TablePagination
